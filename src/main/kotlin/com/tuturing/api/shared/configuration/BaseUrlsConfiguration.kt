package com.tuturing.api.shared.configuration

import com.tuturing.api.shared.valueobject.BaseUrlsParams
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BaseUrlsConfiguration(
    @NotEmpty @Value("\${tuturing.api-base-url}") val apiBaseUrl: String,
    @NotEmpty @Value("\${tuturing.app-base-url}") val appBaseUrl: String,
    @NotEmpty @Value("\${tuturing.cdn-base-url}") val cdnBaseUrl: String
) {
    @Bean
    fun baseUrlsParams(): BaseUrlsParams {
        return BaseUrlsParams(
            apiBaseUrl,
            appBaseUrl,
            cdnBaseUrl
        )
    }
}
