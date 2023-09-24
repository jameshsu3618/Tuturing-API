package com.tuturing.api.location.controller

import com.tuturing.api.location.domain.AutocompleteService
import com.tuturing.api.location.dto.CoordinatesSearchResponseDto
import com.tuturing.api.location.mapper.CoordinatesSearchResponseMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/locations/autocomplete")
class AutocompleteController(
    @Autowired private val autocompleteService: AutocompleteService,
    @Autowired private val coordinatesSearchResponseMapper: CoordinatesSearchResponseMapper
) {
    @GetMapping("")
    fun search(@RequestParam query: String): List<CoordinatesSearchResponseDto> {
        return autocompleteService.findAirportsAndLocations(query).map {
            coordinatesSearchResponseMapper.convertToDto(it)
        }
    }
}
