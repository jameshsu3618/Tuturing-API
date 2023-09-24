package com.tuturing.api.user.domain

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.company.valueobject.CompanyStatus
import com.tuturing.api.legacy.LegacyApiClient
import com.tuturing.api.shared.jms.EmailAddress
import com.tuturing.api.shared.jms.SendEmailMessage
import com.tuturing.api.shared.jms.SendEmailProducer
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.valueobject.PasswordServiceParams
import com.tuturing.api.user.valueobject.UserStatus
import java.util.*
import kotlin.collections.HashMap
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private const val USER_HASH_PASSWORD_SUBSTRING_LENGTH = 24

@Service
class PasswordService(
    @Autowired private val encoder: PasswordEncoder,
    @Autowired private val userService: UserService,
    @Autowired private val emailSender: SendEmailProducer,
    @Autowired private val params: PasswordServiceParams,
    @Autowired private val legacyApiClient: LegacyApiClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun isPasswordValid(password: String): Boolean {
        return password.length >= params.minPasswordLength
    }

    fun encodePassword(password: String): String {
        return encoder.encode(password)
    }

    fun triggerPasswordReset(email: String): Boolean {
        val user = userService.findByEmail(email)

        return if (null != user) {
            triggerPasswordReset(user)
        } else {
            legacyApiClient.resetPassword(email)
        }
    }

    fun triggerPasswordReset(user: UserEntity): Boolean {
        if (UserStatus.ACTIVATED != user.status || CompanyStatus.ACTIVATED != user.company.status) {
            logger.debug("Could not trigger password reset for user {} because user status is {} and company status is {}", user.id.toString(), user.status.name, user.company.status.name)

            return false
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, params.passwordResetExpirationTime)

        val algorithm = Algorithm.RSA256(params.jwtPublicKey, params.jwtPrivateKey)

        val jwt = JWT.create()
            .withIssuer(params.jwtIssuer)
            .withExpiresAt(calendar.time)
            .withClaim("email", user.email)
            .withClaim("userId", user.id.toString())
            .withClaim("token", oneTimeToken(user, calendar))
            .sign(algorithm)

        val emailTemplateVariables = HashMap<String, String>()
        emailTemplateVariables.put("first_name", user.profile.firstName)
        emailTemplateVariables.put("company", user.company.name)
        emailTemplateVariables.put("reset_url", params.appBaseUrl + "/user/password/reset/" + jwt)
        emailTemplateVariables.put("cdn_url", params.cdnBaseUrl)

        emailSender.sendEmail(SendEmailMessage(
            params.defaultEmailSender,
            listOf(EmailAddress(user.profile.fullName, user.email)),
            params.passwordResetSubject,
            params.passwordResetTriggerTemplate,
            jacksonObjectMapper().writeValueAsString(emailTemplateVariables)
        ))

        logger.debug("Reset password for user {} has been sent to {}", user.id.toString(), user.email)

        return true
    }

    fun resetPassword(token: String, password: String): Boolean {
        val jwt = JWT.decode(token)

        val userId = jwt.getClaim("userId").asString()
        val user = userService.findById(UUID.fromString(userId))

        if (null == user) {
            logger.debug("Could not reset password for user {} because there is no such user", userId)

            return false
        }

        if (UserStatus.ACTIVATED != user.status) {
            logger.debug("Could not reset password for user {} because status is {}", user.id.toString(), user.status.name)

            return false
        }

        val algorithm = Algorithm.RSA256(params.jwtPublicKey, params.jwtPrivateKey)

        val verifier = JWT.require(algorithm)
            .withIssuer(params.jwtIssuer)
            .build()

        val oneTimeTokenVerifier = JWT.require(Algorithm.HMAC256(userSecret(user)))
            .withIssuer(params.jwtIssuer)
            .build()

        try {
            verifier.verify(token)
            oneTimeTokenVerifier.verify(jwt.getClaim("token").asString())
        } catch (e: JWTVerificationException) {
            logger.debug("Could not reset password for user {} because JWT verification failed", user.id.toString())

            return false
        }

        if (password.length < params.minPasswordLength) {
            logger.debug("Could not reset password for user {} because password is too short", user.id.toString())

            return false
        }

        user.password = encoder.encode(password)
        userService.save(user)

        logger.debug("Password of user {} has been reset", user.id.toString())

        val emailTemplateVariables = HashMap<String, String>()
        emailTemplateVariables.put("first_name", user.profile.firstName)
        emailTemplateVariables.put("email", user.email)
        emailTemplateVariables.put("profile", params.appBaseUrl + "/user")
        emailTemplateVariables.put("cdn_url", params.cdnBaseUrl)

        emailSender.sendEmail(SendEmailMessage(
            params.defaultEmailSender,
            listOf(EmailAddress(user.profile.fullName, user.email)),
            params.passwordResetSubject,
            params.passwordResetSuccessTemplate,
            jacksonObjectMapper().writeValueAsString(emailTemplateVariables)
        ))

        return true
    }

    @PreAuthorize("#tuturing.isPrincipal(#user)")
    fun changePassword(user: UserEntity, oldPassword: String, newPassword: String): Boolean {
        if (!encoder.matches(oldPassword, user.password)) {
            return false
        }

        if (!this.isPasswordValid(newPassword)) {
            return false
        }

        user.password = encoder.encode((newPassword))
        userService.save(user)

        logger.debug("Password of user {} has been changed", user.id.toString())

        val emailTemplateVariables = HashMap<String, String>()
        emailTemplateVariables.put("first_name", user.profile.firstName)
        emailTemplateVariables.put("email", user.email)
        emailTemplateVariables.put("cdn_url", params.cdnBaseUrl)

        emailSender.sendEmail(SendEmailMessage(
            params.defaultEmailSender,
            listOf(EmailAddress(user.profile.fullName, user.email)),
            params.passwordChangeSubject,
            params.passwordChangeTemplate,
            jacksonObjectMapper().writeValueAsString(emailTemplateVariables)
        ))

        return true
    }

    private fun oneTimeToken(user: UserEntity, expirationDate: Calendar): String {
        val algorithm = Algorithm.HMAC256(userSecret(user))

        val jwt = JWT.create()
            .withIssuer(params.jwtIssuer)
            .withExpiresAt(expirationDate.time)
            .withClaim("userId", user.id.toString())
            .sign(algorithm)

        return jwt.toString()
    }

    private fun userSecret(user: UserEntity): String {
        return user.id.toString() + user.password?.takeLast(USER_HASH_PASSWORD_SUBSTRING_LENGTH)
    }
}
