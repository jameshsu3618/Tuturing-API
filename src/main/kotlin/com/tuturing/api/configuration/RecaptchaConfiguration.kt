package com.tuturing.api.configuration

import com.tuturing.api.shared.api.recaptcha.RecaptchaApiClient
import javax.validation.constraints.NotEmpty
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RecaptchaConfiguration(
    @Autowired private val okHttpClient: OkHttpClient,
    @NotEmpty @Value("\${tuturing.recaptcha.secretKey}") var secretKey: String,
    @NotEmpty @Value("\${tuturing.recaptcha.scoreThreshold}") var scoreThreshold: Double,
    @NotEmpty @Value("\${tuturing.recaptcha.host}") var host: String
) {
    @Bean
    fun recaptchaV3(): RecaptchaApiClient {
        return RecaptchaApiClient(secretKey, scoreThreshold, okHttpClient, host)
    }
}
