package com.tuturing.api.loyalty.controller

import com.tuturing.api.loyalty.dto.HotelChainDto
import com.tuturing.api.loyalty.mapper.HotelChainMapper
import com.tuturing.api.loyalty.service.HotelChainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/hotel-chains/loyalty-programs")
class HotelChainController(
    @Autowired private val hotelChainService: HotelChainService,
    @Autowired private val hotelChainMapper: HotelChainMapper
) {
    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getHotelChains(): ResponseEntity<List<HotelChainDto>> {
        val hotelChains = hotelChainService.findAllHavingLoyaltyProgram()

        val dto = hotelChains.map { hotelChain -> hotelChainMapper.convertToDto(hotelChain) }

        return ResponseEntity.ok(dto)
    }
}
