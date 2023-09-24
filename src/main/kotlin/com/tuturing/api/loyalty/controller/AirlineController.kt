package com.tuturing.api.loyalty.controller

import com.tuturing.api.loyalty.dto.AirlineDto
import com.tuturing.api.loyalty.mapper.AirlineMapper
import com.tuturing.api.loyalty.service.AirlineService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/airlines/loyalty-programs")
class AirlineController(
    @Autowired private val airlineService: AirlineService,
    @Autowired private val airlineMapper: AirlineMapper
) {
    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getAirlines(): ResponseEntity<List<AirlineDto>> {
        val airlines = airlineService.findAllHavingLoyaltyProgram()

        val dto = airlines.map { airline -> airlineMapper.convertToDto(airline) }

        return ResponseEntity.ok(dto)
    }
}
