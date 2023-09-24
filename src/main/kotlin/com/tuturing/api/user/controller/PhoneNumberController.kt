package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.PhoneNumberService
import com.tuturing.api.user.dto.resource.PhoneNumberDto
import com.tuturing.api.user.entity.PhoneNumberEntity
import com.tuturing.api.user.mapper.PhoneNumberMapper
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/phones")
@Validated
class PhoneNumberController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val phoneNumberService: PhoneNumberService,
    @Autowired private val phoneNumberMapper: PhoneNumberMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getPhones(): ResponseEntity<List<PhoneNumberDto>> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val userProfile = principal.user.profile

        val phones = phoneNumberService.findAllById(userProfile)

        val dto = phones.map { phone -> phoneNumberMapper.convertToDto(phone) }

        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("")
    fun addPhone(@RequestBody phoneNumberDto: PhoneNumberDto): ResponseEntity<PhoneNumberDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val userProfile = principal.user.profile

        var phoneNumberEntity = PhoneNumberEntity()

        try {
            phoneNumberEntity = phoneNumberMapper.convertToEntity(phoneNumberDto, phoneNumberEntity)
        } catch (iae: IllegalArgumentException) {
            // could not map, most likely invalid request, e.g. non-existent country code
            return ResponseEntity.badRequest().build()
        }

        phoneNumberEntity.userProfile = userProfile
        phoneNumberService.save(phoneNumberEntity)

        return ResponseEntity.ok(phoneNumberMapper.convertToDto(phoneNumberEntity))
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("/{id}")
    fun updatePhone(@PathVariable id: UUID, @RequestBody phoneNumberDto: PhoneNumberDto): ResponseEntity<PhoneNumberDto> {
        val phoneNumberEntity = phoneNumberService.findById(id)

        return phoneNumberEntity?.let {
            phoneNumberService.save(phoneNumberMapper.convertToEntity(phoneNumberDto, it))
            ResponseEntity.ok(phoneNumberMapper.convertToDto(it))
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @DeleteMapping("/{id}")
    fun deletePhone(@PathVariable id: UUID): ResponseEntity<Any> {
        val phoneNumberEntity = phoneNumberService.findById(id)

        return phoneNumberEntity?.let {
            phoneNumberService.delete(it)
            ResponseEntity.ok().build<Any>()
        } ?: return ResponseEntity.notFound().build()
    }
}
