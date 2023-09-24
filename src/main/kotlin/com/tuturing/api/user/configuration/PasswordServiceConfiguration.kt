package com.tuturing.api.user.configuration

import com.tuturing.api.user.valueobject.PasswordServiceParams
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PasswordServiceConfiguration(
    @Qualifier("jwtPublicKey") @Autowired private val jwtPublicKey: RSAPublicKey,
    @Qualifier("jwtPrivateKey") @Autowired private val jwtPrivateKey: RSAPrivateKey,
    @NotEmpty @Value("\${tuturing.security.password.min-length}") val minPasswordLength: Int,
    @NotEmpty @Value("\${tuturing.security.jwt.issuer}") val jwtIssuer: String,
    @NotEmpty @Value("\${tuturing.app-base-url}") val appBaseUrl: String,
    @NotEmpty @Value("\${tuturing.cdn-base-url}") val cdnBaseUrl: String,
    @NotEmpty @Value("\${tuturing.emails.default-sender}") val defaultEmailSender: String,
    @NotEmpty @Value("\${tuturing.emails.password-change.template}") val passwordChangeTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.password-change.subject}") val passwordChangeSubject: String,
    @NotEmpty @Value("\${tuturing.emails.password-reset.trigger.expiration}") val passwordResetExpirationTime: Int,
    @NotEmpty @Value("\${tuturing.emails.password-reset.trigger.template}") val passwordResetTriggerTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.password-reset.success.template}") val passwordResetSuccessTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.password-reset.subject}") val passwordResetSubject: String
) {
    @Bean
    fun passwordServiceConfig(): PasswordServiceParams {
        return PasswordServiceParams(
            minPasswordLength,
            jwtPublicKey,
            jwtPrivateKey,
            jwtIssuer,
            passwordResetExpirationTime,
            appBaseUrl,
            cdnBaseUrl,
            defaultEmailSender,
            passwordChangeTemplate,
            passwordChangeSubject,
            passwordResetTriggerTemplate,
            passwordResetSuccessTemplate,
            passwordResetSubject
        )
    }
}
