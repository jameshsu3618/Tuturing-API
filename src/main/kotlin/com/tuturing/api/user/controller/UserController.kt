package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.dto.error.ErrorResponse
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.PasswordService
import com.tuturing.api.user.domain.UserRegistrationService
import com.tuturing.api.user.domain.UserService
import com.tuturing.api.user.domain.exception.UserVerificationException
import com.tuturing.api.user.dto.command.ChangePasswordDto
import com.tuturing.api.user.dto.command.ResetPasswordDto
import com.tuturing.api.user.dto.command.TriggerPasswordResetDto
import com.tuturing.api.user.dto.command.VerifyDto
import com.tuturing.api.user.dto.resource.UserDto
import com.tuturing.api.user.mapper.UserMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Validated
class UserController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val userService: UserService,
    @Autowired private val passwordService: PasswordService,
    @Autowired private val userRegistrationService: UserRegistrationService,
    @Autowired private val userMapper: UserMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getSignedInUser(): ResponseEntity<UserDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val user = principal.user

        return ResponseEntity.ok(userMapper.convertToDto(user))
    }

    @PostMapping("/password/trigger-reset")
    fun triggerPasswordReset(@RequestBody dto: TriggerPasswordResetDto): ResponseEntity<Any> {
        passwordService.triggerPasswordReset(dto.email)

        return ResponseEntity.ok().build()
    }

    @PostMapping("/password/reset")
    fun resetPassword(@RequestBody dto: ResetPasswordDto): ResponseEntity<Any> {
        return if (passwordService.resetPassword(dto.token, dto.password)) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("/password/change")
    fun changePassword(@RequestBody dto: ChangePasswordDto): ResponseEntity<Any> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val user = principal.user

        return if (passwordService.changePassword(user, dto.oldPassword, dto.newPassword)) {
            ResponseEntity.ok().build<Any>()
        } else {
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }
    }

    @PostMapping("/verify")
    fun verify(@RequestBody dto: VerifyDto): ResponseEntity<Any> {
        return userRegistrationService.verify(dto.token, dto.password, dto.firstName, dto.lastName).fold({
            ResponseEntity.ok().build()
        }, { exception -> when (exception) {
            is UserVerificationException -> ErrorResponse.unprocessableEntity(
                exception.reason, exception.message
            )
            else -> {
                logger.error("User verification failed", exception)
                ErrorResponse.internalServerError()
            }
        } })
    }
}
