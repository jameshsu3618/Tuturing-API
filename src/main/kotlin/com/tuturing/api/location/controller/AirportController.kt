package com.tuturing.api.location.controller

import com.tuturing.api.location.domain.AirportService
import com.tuturing.api.location.dto.AirportDto
import com.tuturing.api.location.mapper.AirportMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/locations/airports")
class AirportController(
    @Autowired private val airportService: AirportService,
    @Autowired private val airportMapper: AirportMapper
) {

    @GetMapping("")
    fun airports(@RequestParam query: String): ResponseEntity<List<AirportDto>> {
        if (query.length < QUERY_LENGTH_MIN) return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        val airports = airportMapper.convertToDtoList(airportService.findAll(query))
        return ResponseEntity.ok(airports)
    }

    companion object {
        private const val QUERY_LENGTH_MIN = 3
    }
}
