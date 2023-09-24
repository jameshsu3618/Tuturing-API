package com.tuturing.api.user.domain

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.company.entity.DepartmentEntity
import com.tuturing.api.shared.jms.EmailAddress
import com.tuturing.api.shared.jms.SendEmailMessage
import com.tuturing.api.shared.jms.SendEmailProducer
import com.tuturing.api.user.domain.exception.UserRegistrationException
import com.tuturing.api.user.domain.exception.UserVerificationException
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserRegistrationParams
import com.tuturing.api.user.valueobject.UserStatus
import java.time.LocalDateTime
import java.util.Calendar
import java.util.UUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegistrationService(
    @Autowired private val userService: UserService,
    @Autowired private val userProfileService: UserProfileService,
    @Autowired private val passwordService: PasswordService,
    @Autowired private val emailSender: SendEmailProducer,
    @Autowired private val params: UserRegistrationParams
) {
    private val allowedRegistrationRoles = listOf(Role.EMPLOYEE, Role.COMPANY_ADMIN)
    private val allowedSendInvitationStatuses = listOf(UserStatus.CREATED, UserStatus.INVITED)

    @Transactional
    @PreAuthorize("(#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()) " +
        "and #tuturing.isDepartmental(#department)")
    fun register(
        department: DepartmentEntity,
        user: UserEntity,
        profile: UserProfileEntity
    ): Result<UserEntity> {
        if (user.role !in allowedRegistrationRoles) {
            return Result.failure(UserRegistrationException.RoleNotAllowed(user.role))
        }

        val existingUser = userService.findByEmail(user.email)

        if (null != existingUser) {
            return Result.failure(UserRegistrationException.AlreadyRegistered(user.email))
        }

        user.status = UserStatus.CREATED
        user.company = department.company
        user.department = department

        userService.save(user)

        profile.user = user
        profile.company = department.company
        profile.department = department
        userProfileService.save(profile)

        user.profile = profile

        return Result.success(user)
    }

    @PreAuthorize("(#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()) " +
        "and #tuturing.isPrincipal(#inviter) " +
        "and #tuturing.isDepartmental(#user)")
    fun sendEmployeeInvitationEmail(user: UserEntity, inviter: UserEntity): Boolean {
        if (user.status !in allowedSendInvitationStatuses) {
            return false
        }

        user.status = UserStatus.INVITED
        user.inviter = inviter
        user.invitedAt = LocalDateTime.now()
        userService.save(user)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, params.invitationExpirationTime)

        val algorithm = Algorithm.RSA256(params.jwtPublicKey, params.jwtPrivateKey)

        val jwt = JWT.create()
            .withIssuer(params.jwtIssuer)
            .withExpiresAt(calendar.time)
            .withClaim("userId", user.id.toString())
            .withClaim("email", user.email)
            .withClaim("companyName", user.company.name)
            .withClaim("firstName", user.profile.firstName)
            .withClaim("lastName", user.profile.lastName)
            .sign(algorithm)

        val emailTemplateVariables = HashMap<String, String>()
        emailTemplateVariables.put("first_name", user.profile.firstName)
        emailTemplateVariables.put("company", user.company.name)
        emailTemplateVariables.put("invite_url", params.appBaseUrl + "/user/verify/" + jwt)
        emailTemplateVariables.put("cdn_url", params.cdnBaseUrl)

        emailSender.sendEmail(SendEmailMessage(
            params.defaultEmailSender,
            listOf(EmailAddress(user.profile.fullName, user.email)),
            params.userVerificationSubject,
            params.userVerificationTemplate,
            jacksonObjectMapper().writeValueAsString(emailTemplateVariables)
        ))

        return true
    }

    fun verify(token: String, password: String, firstName: String, lastName: String): Result<UserEntity> {
        // decode token
        val jwt = try {
            JWT.decode(token)
        } catch (e: Exception) {
            return Result.failure(UserVerificationException.TokenInvalid)
        }

        // find entities associated with the token
        val user = userService.findById(
            UUID.fromString(jwt.getClaim("userId").asString())
        )

        // if entities can not be found the token is invalid
        if (null == user) {
            return Result.failure(UserVerificationException.TokenInvalid)
        }

        // verify the token
        val algorithm = Algorithm.RSA256(params.jwtPublicKey, params.jwtPrivateKey)

        val verifier = JWT.require(algorithm)
            .withIssuer(params.jwtIssuer)
            .build()

        try {
            verifier.verify(token)
        } catch (e: JWTVerificationException) {
            return Result.failure(UserVerificationException.TokenInvalid)
        }

        // verify user status
        if (UserStatus.INVITED != user.status) {
            return Result.failure(UserVerificationException.AlreadyVerified)
        }

        // check password rules
        if (!passwordService.isPasswordValid(password)) {
            return Result.failure(UserVerificationException.InvalidPassword)
        }

        // set password, statues, and save
        user.password = passwordService.encodePassword(password)
        user.status = UserStatus.ACTIVATED
        userService.save(user)

        // Re-save profile name
        val profile = user.profile
        profile.firstName = firstName
        profile.lastName = lastName
        userProfileService.save(profile)

        val emailTemplateVariables = HashMap<String, String>()
        emailTemplateVariables.put("first_name", user.profile.firstName)
        emailTemplateVariables.put("cdn_url", params.cdnBaseUrl)

        // check user's role to map admin guide url or employee guide url as template variable
        if (user.role === Role.EMPLOYEE) {
            emailTemplateVariables.put("user_guide_url", params.userGuideUrl)
        } else {
            emailTemplateVariables.put("user_guide_url", params.adminGuideUrl)
        }

        emailSender.sendEmail(SendEmailMessage(
            params.defaultEmailSender,
            listOf(EmailAddress(user.profile.fullName, user.email)),
            params.userOnBoardingSubject,
            params.userOnBoardingTemplate,
            jacksonObjectMapper().writeValueAsString(emailTemplateVariables)
        ))

        return Result.success(user)
    }
}
