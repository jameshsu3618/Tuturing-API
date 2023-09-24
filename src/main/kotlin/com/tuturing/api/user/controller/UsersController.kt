package com.tuturing.api.user.controller

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.dto.error.ErrorResponse
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.domain.UserRegistrationService
import com.tuturing.api.user.domain.UserService
import com.tuturing.api.user.domain.exception.UserRegistrationException
import com.tuturing.api.user.dto.resource.admin.UserDto
import com.tuturing.api.user.dto.resource.admin.UserRegistrationDto
import com.tuturing.api.user.dto.resource.admin.UserUpdateDto
import com.tuturing.api.user.dto.resource.admin.UserWithInviterDto
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.mapper.admin.AdminUserMapper
import com.tuturing.api.user.mapper.admin.AdminUserProfileMapper
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Validated
class UsersController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired val userService: UserService,
    @Autowired val userRegistrationService: UserRegistrationService,
    @Autowired val userMapper: AdminUserMapper,
    @Autowired val userProfileMapper: AdminUserProfileMapper
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun findUsers(@RequestParam offset: Int, @RequestParam count: Int): List<UserDto> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val company = principal.user.company

        return userService.findByCompany(company, offset, count).map {
            userMapper.convertToDto(it)
        }
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("")
    fun register(@RequestBody dto: UserRegistrationDto): ResponseEntity<Any> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        var user = UserEntity()
        user = userMapper.convertToEntity(dto, user)

        var profile = UserProfileEntity()
        profile = userProfileMapper.convertToEntity(dto.profile, profile)

        return userRegistrationService.register(
            principal.user.department,
            user,
            profile
        ).fold({
            ResponseEntity.ok(userMapper.convertToDto(it))
        }, { exception -> when (exception) {
            is UserRegistrationException -> ErrorResponse.unprocessableEntity(
                exception.reason, exception.message
            )
            else -> {
                logger.error("User registration failed", exception)
                ErrorResponse.internalServerError()
            }
        } })
    }
    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("/{id}")
    fun findUser(@PathVariable id: UUID): ResponseEntity<UserWithInviterDto> {
        val user = userService.findByIdAuthorized(id)

        return user?.let {
            ResponseEntity.ok(userMapper.convertToDtoWithInviter(it))
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody dto: UserUpdateDto): ResponseEntity<UserDto> {
        val user = userService.findById(id)

        return user?.let {
            if (!userService.changeRole(user, dto.role)) {
                ResponseEntity.status(HttpStatus.FORBIDDEN)
            } else {
                val params = userMapper.convertToUserUpdateParams(dto)
                userService.changeNameAndEmailAndPolicy(user, params.profile.firstName, params.profile.lastName, params.email, params.policy)
            }

            ResponseEntity.ok(userMapper.convertToDto(user))
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("/{id}/invite")
    fun invite(@PathVariable id: UUID): ResponseEntity<Any> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails

        val user = userService.findById(id)

        return user?.let {
            return if (userRegistrationService.sendEmployeeInvitationEmail(it, principal.user)) {
                ResponseEntity.ok().build<Any>()
            } else {
                ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
            }
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("/{id}/deactivate")
    fun deactivate(@PathVariable id: UUID): ResponseEntity<Any> {
        val user = userService.findById(id)

        return user?.let {
            userService.deactivate(it)

            ResponseEntity.ok().build<Any>()
        } ?: return ResponseEntity.notFound().build()
    }

    @PreAuthorize("#oauth2.isUser()")
    @PostMapping("/{id}/activate")
    fun activate(@PathVariable id: UUID): ResponseEntity<Any> {
        val user = userService.findById(id)

        return user?.let {
            userService.activate(it)

            ResponseEntity.ok().build<Any>()
        } ?: return ResponseEntity.notFound().build()
    }
}
