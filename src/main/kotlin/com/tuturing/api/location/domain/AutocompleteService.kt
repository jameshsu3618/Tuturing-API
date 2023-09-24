package com.tuturing.api.location.domain

import com.tuturing.api.location.mapper.AutocompleteMapper
import com.tuturing.api.location.mapper.CoordinatesSearchResponseMapper
import com.tuturing.api.location.valueobject.CoordinatesSearchResponse
import com.tuturing.api.location.valueobject.LocationType
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AutocompleteService(
    @Autowired private val coordinatesService: CoordinatesService,
    @Autowired private val coordinatesSearchResponseMapper: CoordinatesSearchResponseMapper,
    @Autowired private val airportService: AirportService,
    @Autowired private val autocompleteMapper: AutocompleteMapper,
    @NotEmpty @Value("\${tuturing.autocomplete.airport-limit}") val airportLimit: Int
) {
    fun findAirportsAndLocations(query: String): List<CoordinatesSearchResponse> {
        val airports = airportService.findAirports(query).map {
            autocompleteMapper.convertToResponse(it)
        }
        val coordinatesSearchResponse = coordinatesService.search(query)

        val response = mutableListOf<CoordinatesSearchResponse>()

        airports.filter {
            it.type == LocationType.AIRPORT
        }.take(airportLimit).forEach {
            response.add(it)
        }

        coordinatesSearchResponse.forEach {
            response.add(it)
        }

        return response
    }
}
