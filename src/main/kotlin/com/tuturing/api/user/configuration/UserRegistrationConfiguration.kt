package com.tuturing.api.user.configuration

import com.tuturing.api.user.valueobject.UserRegistrationParams
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserRegistrationConfiguration(
    @Qualifier("jwtPublicKey") @Autowired private val jwtPublicKey: RSAPublicKey,
    @Qualifier("jwtPrivateKey") @Autowired private val jwtPrivateKey: RSAPrivateKey,
    @NotEmpty @Value("\${tuturing.security.jwt.issuer}") val jwtIssuer: String,
    @NotEmpty @Value("\${tuturing.app-base-url}") val appBaseUrl: String,
    @NotEmpty @Value("\${tuturing.cdn-base-url}") val cdnBaseUrl: String,
    @NotEmpty @Value("\${tuturing.user-guide-url}") val userGuideUrl: String,
    @NotEmpty @Value("\${tuturing.admin-guide-url}") val adminGuideUrl: String,
    @NotEmpty @Value("\${tuturing.emails.default-sender}") val defaultEmailSender: String,
    @NotEmpty @Value("\${tuturing.emails.user-verification.expiration}") val invitationExpirationTime: Int,
    @NotEmpty @Value("\${tuturing.emails.user-verification.template}") val userVerificationTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.user-verification.subject}") val userVerificationSubject: String,
    @NotEmpty @Value("\${tuturing.emails.user-on-boarding.template}") val userOnBoardingTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.user-on-boarding.subject}") val userOnBoardingSubject: String
) {
    @Bean
    fun userRegistrationConfig(): UserRegistrationParams {
        return UserRegistrationParams(
            jwtPublicKey,
            jwtPrivateKey,
            jwtIssuer,
            invitationExpirationTime,
            appBaseUrl,
            cdnBaseUrl,
            userGuideUrl,
            adminGuideUrl,
            defaultEmailSender,
            userVerificationTemplate,
            userVerificationSubject,
            userOnBoardingTemplate,
            userOnBoardingSubject
        )
    }
}
