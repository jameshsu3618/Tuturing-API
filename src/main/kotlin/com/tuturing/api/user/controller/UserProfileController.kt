package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.UserProfileService
import com.tuturing.api.user.dto.resource.UserProfileDto
import com.tuturing.api.user.mapper.UserProfileMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/profile")
@Validated
class UserProfileController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val userProfileMapper: UserProfileMapper,
    @Autowired private val userProfileService: UserProfileService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getProfile(): ResponseEntity<UserProfileDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val userProfile = principal.user.profile

        return ResponseEntity.ok(userProfileMapper.convertToDto(userProfile))
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("")
    fun updateProfile(@RequestBody profileDto: UserProfileDto): ResponseEntity<UserProfileDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val profileEntity = principal.user.profile

        userProfileService.save(userProfileMapper.convertToEntity(profileDto, profileEntity))

        return ResponseEntity.ok(userProfileMapper.convertToDto(profileEntity))
    }
}
