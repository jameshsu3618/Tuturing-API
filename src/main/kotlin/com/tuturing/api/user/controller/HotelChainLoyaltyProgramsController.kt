package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.HotelChainLoyaltyProgramService
import com.tuturing.api.user.domain.UserService
import com.tuturing.api.user.dto.resource.HotelChainLoyaltyProgramDto
import com.tuturing.api.user.entity.HotelChainLoyaltyProgramEntity
import com.tuturing.api.user.mapper.HotelChainLoyaltyProgramMapper
import java.util.*
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/loyalty-programs/hotel-chains")
class HotelChainLoyaltyProgramsController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val userService: UserService,
    @Autowired private val hotelChainLoyaltyProgramService: HotelChainLoyaltyProgramService,
    @Autowired private val hotelChainLoyaltyMapper: HotelChainLoyaltyProgramMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getLoyaltyPrograms(): ResponseEntity<List<HotelChainLoyaltyProgramDto>> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val loyaltyPrograms = hotelChainLoyaltyProgramService.findByUserProfile(principal.user.profile)

        val dto = loyaltyPrograms.map { loyaltyProgram -> hotelChainLoyaltyMapper.convertToDto(loyaltyProgram) }

        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("")
    fun addLoyaltyProgram(@Valid @RequestBody loyaltyProgramDto: HotelChainLoyaltyProgramDto): ResponseEntity<HotelChainLoyaltyProgramDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        var loyaltyProgramEntity = HotelChainLoyaltyProgramEntity()

        try {
            loyaltyProgramEntity = hotelChainLoyaltyMapper.convertToEntity(loyaltyProgramDto, loyaltyProgramEntity)
        } catch (iae: IllegalArgumentException) {
            // could not map, most likely invalid request, e.g. non-existent airline
            return ResponseEntity.badRequest().build()
        }

        loyaltyProgramEntity.userProfile = principal.user.profile
        hotelChainLoyaltyProgramService.save(loyaltyProgramEntity)

        return ResponseEntity.ok(hotelChainLoyaltyMapper.convertToDto(loyaltyProgramEntity))
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("/{id}")
    fun updateLoyaltyProgram(@PathVariable id: UUID, @Valid @RequestBody loyaltyProgramDto: HotelChainLoyaltyProgramDto): ResponseEntity<HotelChainLoyaltyProgramDto> {
        val loyaltyProgramEntity = hotelChainLoyaltyProgramService.findById(id)

        return loyaltyProgramEntity?.let {
            val entity = hotelChainLoyaltyMapper.convertToEntity(loyaltyProgramDto, it)
            hotelChainLoyaltyProgramService.save(entity)
            ResponseEntity.ok(hotelChainLoyaltyMapper.convertToDto(it))
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @DeleteMapping("/{id}")
    fun deleteLoyaltyProgram(@PathVariable id: UUID): ResponseEntity<Any> {
        val loyaltyProgramEntity = hotelChainLoyaltyProgramService.findById(id)

        return loyaltyProgramEntity?.let {
            hotelChainLoyaltyProgramService.delete(it)
            ResponseEntity.ok().build<Any>()
        } ?: return ResponseEntity.notFound().build()
    }
}
