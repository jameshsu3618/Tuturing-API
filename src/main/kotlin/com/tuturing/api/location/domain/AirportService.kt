package com.tuturing.api.location.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.itinerary.valueobject.CachedAutocompleteResults
import com.tuturing.api.location.entity.AirportEntity
import com.tuturing.api.location.mapper.AirportMapper
import com.tuturing.api.location.repository.AirportRepository
import com.tuturing.api.location.valueobject.Airport
import com.tuturing.api.location.valueobject.LocationType
import com.tuturing.api.location.valueobject.MultiAirportCity
import com.tuturing.api.sabre.client.rest.GEO_SEARCH_MODE
import com.tuturing.api.sabre.client.rest.GEO_SEARCH_VERSION
import com.tuturing.api.sabre.client.rest.valueobject.GeoAutocompleteCategory
import com.tuturing.api.shared.valueobject.Distance
import com.tuturing.api.shared.valueobject.DistanceUnit
import com.sabre.rest.apis.GeoAutocompleteApi
import com.sabre.rest.apis.GeoSearchApi
import com.sabre.rest.models.GeoSearchRequest
import com.sabre.rest.models.GeoSearchRequestGeoSearchRQ
import com.sabre.rest.models.GeoSearchRequestGeoSearchRQGeoRef
import com.sabre.rest.models.GeoSearchRequestGeoSearchRQGeoRefGeoCode
import com.sabre.rest.models.GeoSearchResponse
import java.math.BigDecimal
import java.time.Duration
import javax.validation.constraints.NotEmpty
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

private const val AIRPORTS_RESULTS_LIMIT = 5
private const val CITIES_RESULTS_LIMIT = 5
private const val AUTOCOMPLETE_CACHE_PREFIX = "airport:autocomplete_result_v1:"
private const val AUTOCOMPLETE_CACHE_TIME = 60 * 60 * 24 * 30 // a month

@Service
class AirportService(
    @Autowired private val geoAutocompleteApi: GeoAutocompleteApi,
    @Autowired private val geoSearchApi: GeoSearchApi,
    @Autowired private val airportRepository: AirportRepository,
    @Autowired private val airportMapper: AirportMapper,
    @Autowired private val coordinatesService: CoordinatesService,
    @Autowired private val stringRedisTemplate: StringRedisTemplate,
    @Autowired @Qualifier("macToAirportsMap") private val macToAirportsMap: Map<String, MultiAirportCity>,
    @Autowired @Qualifier("airportToMac") private val airportToMacMap: Map<String, MultiAirportCity>,
    @NotEmpty @Value("\${tuturing.sabre.geo-autocomplete.min-ranking}") private val minRanking: Int,
    @NotEmpty @Value("\${tuturing.sabre.geo-search.radius-miles}") private val geoSearchRadius: BigDecimal
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // new implementation

    fun findAll(query: String): List<Airport> {
        return runBlocking {
            logger.debug("Airport autocomplete: starts")
            val cachedResult = findCachedAirports(query)

            if (null != cachedResult) {
                cachedResult // return cached result
            } else {
                val deferreds = mutableListOf<Deferred<List<Airport>>>()

                logger.debug("Airport autocomplete: in-memory mac lookup")
                // in memory lookup, no need to run in parallel
                val airportsFoundInMacs = findInMacs(query)

                deferreds.add(async(Dispatchers.IO) {
                    logger.debug("Airport autocomplete: start sabre search")
                    findInSabre(query, AIRPORTS_RESULTS_LIMIT)
                })

                deferreds.add(async(Dispatchers.IO) {
                    logger.debug("Airport autocomplete: start mapbox search")
                    findInMapbox(query, CITIES_RESULTS_LIMIT)
                })

                logger.debug("Airport autocomplete: getting sabre and mapbox search results")
                var airports = deferreds.awaitAll().flatten()

                logger.debug("Airport autocomplete: second in-memory mac search for found airports")
                val macsFoundViaAirports = airports.filter {
                    it.iataCode != null
                }.map { airport ->
                    findInMacs(airport.iataCode!!)
                }.flatten().distinctBy { it.iataCode }

                logger.debug("Airport autocomplete: picking a mac to show in search results")
                var macsToReturn = if (0 < airportsFoundInMacs.size) {
                    airportsFoundInMacs
                } else if (0 < macsFoundViaAirports.size) {
                    macsFoundViaAirports.take(1)
                } else {
                    listOf()
                }

                if (0 == macsToReturn.size) {
                    if (airports.isNotEmpty() && airports[0].latitude != null && airports[0].longitude != null) {
                        val nearby = findNearby(airports[0].latitude!!, airports[0].longitude!!)
                            .filter { it.iataCode != airports[0].iataCode }
                        val nearbyCodes = nearby.map { it.iataCode }
                        airports = airports.take(1) + airports.takeLast(airports.size - 1).filter { it.iataCode !in nearbyCodes }
                        airports[0].nearbyAirports = nearby
                    }
                }

                logger.debug("Airport autocomplete: merging search results")
                val mergedResults = macsToReturn.union(airports).toList()

                logger.debug("Airport autocomplete: moving exact match by the airport code to the top")
                val exactMatches = mergedResults.filter { airport ->
                    airport.iataCode?.toUpperCase() == query.toUpperCase()
                }
                logger.debug("Airport autocomplete: exact matches {}", exactMatches)

                val finalList = if (exactMatches.size > 0) {
                    // ignore MAC, if the airport is pushed to the top of the list, MAC isn't
                    val otherMatches = airports.filter { airport ->
                        airport.iataCode?.toUpperCase() != query.toUpperCase()
                    }
                    exactMatches.union(otherMatches).toList()
                } else {
                    mergedResults
                }

                cacheQueryResults(query, finalList)

                finalList // return merged results
            }
        }
    }

    private fun cacheQueryResults(query: String, autocompleteResults: List<Airport>) {
        val sha256hex: String = DigestUtils.sha256Hex(query)
        val key = AUTOCOMPLETE_CACHE_PREFIX + sha256hex
        val cachedData = CachedAutocompleteResults(query, autocompleteResults)
        val json = jacksonObjectMapper().writeValueAsString(cachedData)

        logger.debug("Writing to airport autocomplete cache: {} {}", key, json)

        stringRedisTemplate.opsForValue().set(key, json, Duration.ofSeconds(AUTOCOMPLETE_CACHE_TIME.toLong()))
    }

    private fun findCachedAirports(query: String): List<Airport>? {
        val sha256hex: String = DigestUtils.sha256Hex(query)
        val key = AUTOCOMPLETE_CACHE_PREFIX + sha256hex
        val value = stringRedisTemplate.opsForValue().get(key)

        logger.debug("Reading from airport autocomplete cache: {}", key)

        return if (null != value) {
            try {
                val cachedData = jacksonObjectMapper().readValue(value, CachedAutocompleteResults::class.java)
                cachedData.airports
            } catch (e: Throwable) {
                null
            }
        } else {
            null
        }
    }

    private fun macToAirport(mac: MultiAirportCity): Airport {
        return Airport(
            iataCode = mac.iataCode,
            name = mac.name,
            city = null,
            region = null,
            country = mac.countryCode,
            latitude = null,
            longitude = null,
            distance = null,
            nearbyAirports = mac.airports?.map { airport ->
                Airport(
                    iataCode = airport.iataCode,
                    name = airport.name,
                    city = airport.city,
                    region = airport.region,
                    country = airport.country,
                    latitude = airport.latitude,
                    longitude = airport.longitude,
                    distance = null,
                    nearbyAirports = null,
                    type = LocationType.AIRPORT
                )
            } ?: listOf(),
            type = LocationType.MAC
        )
    }

    private fun findInMacs(query: String): List<Airport> {
        return if (query.length == MAC_LENGTH) {
            val result = mutableListOf<Airport>()

            macToAirportsMap[query.toUpperCase()]?.let {
                result.add(macToAirport(it))
            }

            airportToMacMap[query.toUpperCase()]?.let {
                result.add(macToAirport(it))
            }

            result.distinctBy { it.iataCode }.toList()
        } else {
            listOf<Airport>()
        }
    }

    private suspend fun findInMapbox(query: String, limit: Int): List<Airport> {
        return runBlocking {
            logger.debug("Airport autocomplete: searching via MapBox: start")

            // val deferreds = mutableListOf<Deferred<List<Airport>>>()

            val places = coordinatesService.search(query).filter {
                it.type == LocationType.CITY
            }

            logger.debug("Airport autocomplete: searching via MapBox: found places: {}", places)

            val deferreds = places.filter { place ->
                null != place.coordinates
            }.map { place ->
                logger.debug("Airport autocomplete: searching via MapBox: adding Sabre geo search by lat/long {}, {}", place.coordinates!!.lat, place.coordinates!!.lon)

                async(Dispatchers.IO) {
                    logger.debug("Airport autocomplete: searching via MapBox: adding Sabre geo search by lat/long {}, {} - adding task", place.coordinates!!.lat, place.coordinates!!.lon)
                    val nearby = findNearby(
                        place.coordinates!!.lat.toBigDecimal(),
                        place.coordinates!!.lon.toBigDecimal()
                    )

                    if (0 < nearby.size) {
                        logger.debug("Airport autocomplete: searching via MapBox: found nearby airports for lat/long {}, {}", place.coordinates!!.lat, place.coordinates!!.lon)
                        val first = nearby.first()
                        listOf(
                            Airport(
                                iataCode = first.iataCode,
                                name = place.name?.split(",")?.get(0) ?: place.name,
                                city = null,
                                region = place.region,
                                country = place.country,
                                latitude = first.latitude,
                                longitude = first.longitude,
                                distance = first.distance,
                                nearbyAirports = first.nearbyAirports,
                                type = LocationType.CITY
                            )
                        )
                    } else {
                        logger.debug("Airport autocomplete: searching via MapBox: did not find nearby airports for lat/long {}, {}", place.coordinates!!.lat, place.coordinates!!.lon)
                        listOf()
                    }
                }
            }

            logger.debug("Airport autocomplete: searching via MapBox: waiting to merge search results")
            deferreds.awaitAll().flatten().distinctBy { it.iataCode }.take(limit)
        }
    }

    private suspend fun findInSabre(query: String, limit: Int): List<Airport> {
        logger.debug("Airport autocomplete: searching via Sabre")
        return findAirports(query).take(limit)
    }

    // the original implementation and helper functions

    private suspend fun autoCompleteAirports(query: String, useMinRanking: Boolean = true): List<Airport> {
        logger.debug("Airport autocomplete: searching via Sabre: autocomplete airports request, query: {}", query)
        val autoCompleteResponse = geoAutocompleteApi.geoAutocomplete(
                query,
                GeoAutocompleteCategory.AIR.toString(),
                GEO_AUTOCOMPLETE_MAX_RESULTS.toBigDecimal())
        logger.debug("Airport autocomplete: searching via Sabre: autocomplete airports response, query: {}", query)
        return autoCompleteResponse.Response?.grouped?.doclist?.docs?.asSequence()
                ?.filter {
                    it.ranking != null &&
                            it.id != null &&
                            it.name != null &&
                            it.ranking != null &&
                            ((useMinRanking && it.ranking!! > minRanking.toBigDecimal()) || !useMinRanking)
                }
                ?.map {
                    Airport(it.id, it.name, it.city, it.state, it.country, it.latitude?.toBigDecimal(),
                            it.longitude?.toBigDecimal(), null, null, LocationType.AIRPORT)
                }
                ?.toList() ?: listOf()
    }

    fun findAirports(query: String, useMinRanking: Boolean = true): List<Airport> {
        logger.debug("Airport autocomplete: searching via Sabre: find airports")
        return runBlocking { autoCompleteAirports(query, useMinRanking) }
    }

    fun findNearby(latitude: BigDecimal, longitude: BigDecimal): List<Airport> {
        logger.debug("Airport autocomplete: searching via MapBox: executing findNearby for lat/long {}, {}", latitude, longitude)
        return runBlocking {
            val resultsPage = 1
            logger.debug("Airport autocomplete: searching via MapBox: executing findNearby for lat/long {}, {} - geosearch", latitude, longitude)
            val geoSearchResponse: GeoSearchResponse = geoSearchApi.geoSearch(GeoSearchRequest(GeoSearchRequestGeoSearchRQ(GEO_SEARCH_VERSION, GeoSearchRequestGeoSearchRQGeoRef(
                    geoSearchRadius,
                    GEO_SEARCH_MAX_RESULTS.toBigDecimal(),
                    resultsPage.toBigDecimal(),
                    null,
                    GeoSearchRequestGeoSearchRQGeoRef.CategoryEnum.AIR,
                    GeoSearchRequestGeoSearchRQGeoRef.UOMEnum.MI,
                    GeoSearchRequestGeoSearchRQGeoRefGeoCode(latitude, longitude),
                    null,
                    null
            ))), GEO_SEARCH_MODE)
            val nearbyAirports = geoSearchResponse.GeoSearchRS?.GeoSearchResults?.GeoSearchResult
                    ?.asSequence()
                    ?.filter { it.Id != null }
                    ?.map {
                        Airport(it.Id, it.Name, it.City, it.State, it.Country, it.Latitude, it.Longitude,
                                it.Distance?.let { it1 -> Distance(it1, DistanceUnit.MI) }, null,
                            LocationType.AIRPORT)
                    }
                    ?.toList() ?: listOf()

            val autoCompleteDeferred = nearbyAirports.map {
                logger.debug("Airport autocomplete: searching via MapBox: executing findNearby for lat/long {}, {} - autocomplete", latitude, longitude)
                async { autoCompleteAirports(it.iataCode!!) }
            }
            logger.debug("Airport autocomplete: searching via MapBox: executing findNearby for lat/long {}, {} - waiting to complete", latitude, longitude)
            val allowedAirportCodes = autoCompleteDeferred.awaitAll()
                    .flatten()
                    .map { it.iataCode }
                    .distinct()

            nearbyAirports.filter {
                it.iataCode in allowedAirportCodes
            }
        }
    }

    /**
     * Returns a list of airports with nearby airports filled for the first airport in the list
     */
    fun findWithNearby(query: String): List<Airport> {
        var airports = findAirports(query)

        if (airports.isNotEmpty() && airports[0].latitude != null && airports[0].longitude != null) {
            val nearby = findNearby(airports[0].latitude!!, airports[0].longitude!!)
                    .filter { it.iataCode != airports[0].iataCode }
            val nearbyCodes = nearby.map { it.iataCode }
            airports = airports.take(1) + airports.takeLast(airports.size - 1).filter { it.iataCode !in nearbyCodes }
            airports[0].nearbyAirports = nearby
        }
        return airports
    }

    private fun findWithMac(query: String): List<Airport> {
        val mac = macToAirportsMap[query.toUpperCase()]

        val airport = mac?.let {
            mac.airports?.forEach { air ->
                air.type = LocationType.AIRPORT
            }
            listOf(Airport(mac.iataCode, mac.name, null, null, mac.countryCode, null, null,
                    null, mac.airports, LocationType.MAC))
        } ?: listOf()

        val macAirports = mac?.airports?.let { airportList ->
            airportList.map { it.iataCode }
        } ?: listOf()

        val moreAirports = findAirports(query).filter { it.iataCode !in macAirports }

        return airport + moreAirports
    }

    fun find(query: String, includeCities: Boolean = false): List<Airport> {
        return if (query.length == MAC_LENGTH && macToAirportsMap[query.toUpperCase()] != null) {
            findWithMac(query)
        } else {
            findWithNearby(query)
        }
    }

    fun findByIataCode(iataCode: String): AirportEntity? {
        return airportRepository.findByIataCode(iataCode)
    }

    fun findAirportCityByIataCode(iataCode: String): String? {
        return findByIataCode(iataCode)?.city
    }

    fun save(airport: Airport): AirportEntity {
        val airportEntity = airportMapper.convertToEntity(airport)
        return airportRepository.save(airportEntity)
    }

    companion object {
        private const val GEO_AUTOCOMPLETE_MAX_RESULTS = 10
        private const val GEO_SEARCH_MAX_RESULTS = 8
        private const val MAC_LENGTH = 3
    }
}
