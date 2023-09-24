package com.tuturing.api.location.domain

import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.tuturing.api.location.valueobject.Coordinates
import com.tuturing.api.location.valueobject.CoordinatesSearchResponse
import com.tuturing.api.location.valueobject.LocationType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CoordinatesService(
    @Autowired private val mapboxToken: String
) {
    fun search(query: String): List<CoordinatesSearchResponse> {
        // TO-DO: Add .proximity() while passing in the user's location to refine autocomplete results
        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(mapboxToken)
            .query(query)
            .autocomplete(true)
            .build()

        var coordinatesSearchResponses = mutableListOf<CoordinatesSearchResponse>()
        val response = mapboxGeocoding.executeCall()

        if (response.isSuccessful()) {
            var features = response.body()!!.features()
            if (features.size > 0) {
                features.forEach { feature ->
                    var region = ""
                    var country = ""
                    var name = feature.placeName()
                    val regex = """([\w\s]+)""".toRegex()
                    feature.context()?.forEach {
                        val type = it.id()?.let { text -> regex.find(text) }
                        // logger.debug("type {}", type?.value)
                        if (type?.value == "region")
                            region = it.text()!!
                        if (type?.value == "country")
                            country = it.text()!!
                        if (type?.value == "poi")
                            name = feature.placeName()?.split(",")?.get(0)
                    }
                    val lon = feature.center()?.coordinates()?.get(0)
                    val lat = feature.center()?.coordinates()?.get(1)
                    if (lon != null && lat != null) {
                        coordinatesSearchResponses.add(
                            CoordinatesSearchResponse(
                                name = name,
                                coordinates = Coordinates(lon, lat),
                                type = feature.placeType()?.first()?.let { placeType ->
                                    placeTypeToLocationType(placeType)
                                },
                                region = region,
                                country = country
                            )
                        )
                    }
                }
            }
        }

        return coordinatesSearchResponses
    }

    private fun placeTypeToLocationType(placeType: String): LocationType {
        return when (placeType.toLowerCase()) {
            "country" -> LocationType.COUNTRY
            "region" -> LocationType.REGION
            "postcode" -> LocationType.AREA
            "district" -> LocationType.AREA
            "place" -> LocationType.CITY
            "locality" -> LocationType.NEIGHBORHOOD
            "neighborhood" -> LocationType.NEIGHBORHOOD
            "address" -> LocationType.ADDRESS
            "poi" -> LocationType.LANDMARK
            else -> LocationType.LANDMARK // it should never happen, the above list is exhaustive
        }
    }
}
