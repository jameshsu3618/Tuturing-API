package com.tuturing.api.location.controller

import com.tuturing.api.location.domain.CoordinatesService
import com.tuturing.api.location.dto.CoordinatesSearchResponseDto
import com.tuturing.api.location.mapper.CoordinatesSearchRequestMapper
import com.tuturing.api.location.mapper.CoordinatesSearchResponseMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/locations/coordinates/search")
class CoordinatesSearchController(
    @Autowired private val coordinatesService: CoordinatesService,
    @Autowired private val coordinatesSearchRequestMapper: CoordinatesSearchRequestMapper,
    @Autowired private val coordinatesSearchResponseMapper: CoordinatesSearchResponseMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("")
    fun searchCoordinates(@RequestParam query: String): ResponseEntity<List<CoordinatesSearchResponseDto>> {
        var response = coordinatesService.search(query)
        val dto = response.map { item -> coordinatesSearchResponseMapper.convertToDto(item) }
        return ResponseEntity.ok(dto)
    }
}
