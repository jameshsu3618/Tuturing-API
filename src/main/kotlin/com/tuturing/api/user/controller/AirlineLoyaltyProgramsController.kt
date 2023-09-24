package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.AirlineLoyaltyProgramService
import com.tuturing.api.user.domain.UserService
import com.tuturing.api.user.dto.resource.AirlineLoyaltyProgramDto
import com.tuturing.api.user.entity.AirlineLoyaltyProgramEntity
import com.tuturing.api.user.mapper.AirlineLoyaltyProgramMapper
import java.util.*
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/loyalty-programs/airlines")
class AirlineLoyaltyProgramsController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val userService: UserService,
    @Autowired private val airlineLoyaltyProgramService: AirlineLoyaltyProgramService,
    @Autowired private val airlineLoyaltyMapper: AirlineLoyaltyProgramMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getLoyaltyPrograms(): ResponseEntity<List<AirlineLoyaltyProgramDto>> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val loyaltyPrograms = airlineLoyaltyProgramService.findAllByUserProfile(principal.user.profile)

        val dto = loyaltyPrograms.map { loyaltyProgram -> airlineLoyaltyMapper.convertToDto(loyaltyProgram) }

        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("")
    fun addLoyaltyProgram(@Valid @RequestBody loyaltyProgramDto: AirlineLoyaltyProgramDto): ResponseEntity<AirlineLoyaltyProgramDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails
        var loyaltyProgramEntity = AirlineLoyaltyProgramEntity()

        try {
            loyaltyProgramEntity = airlineLoyaltyMapper.convertToEntity(loyaltyProgramDto, loyaltyProgramEntity)
        } catch (iae: IllegalArgumentException) {
            // could not map, most likely invalid request, e.g. non-existent airline
            return ResponseEntity.badRequest().build()
        }

        loyaltyProgramEntity.userProfile = principal.user.profile
        airlineLoyaltyProgramService.save(loyaltyProgramEntity)

        return ResponseEntity.ok(airlineLoyaltyMapper.convertToDto(loyaltyProgramEntity))
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("/{id}")
    fun updateLoyaltyProgram(@PathVariable id: UUID, @Valid @RequestBody loyaltyProgramDto: AirlineLoyaltyProgramDto): ResponseEntity<AirlineLoyaltyProgramDto> {
        val loyaltyProgramEntity = airlineLoyaltyProgramService.findById(id)

        return loyaltyProgramEntity?.let {
            val entity = airlineLoyaltyMapper.convertToEntity(loyaltyProgramDto, it)
            airlineLoyaltyProgramService.save(entity)
            ResponseEntity.ok(airlineLoyaltyMapper.convertToDto(it))
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @DeleteMapping("/{id}")
    fun deleteLoyaltyProgram(@PathVariable id: UUID): ResponseEntity<Any> {
        val loyaltyProgramEntity = airlineLoyaltyProgramService.findById(id)

        return loyaltyProgramEntity?.let {
            airlineLoyaltyProgramService.delete(it)
            ResponseEntity.ok().build<Any>()
        } ?: return ResponseEntity.notFound().build()
    }
}
