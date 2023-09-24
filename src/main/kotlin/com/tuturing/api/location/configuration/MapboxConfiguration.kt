package com.tuturing.api.location.configuration

import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapboxConfiguration(
    @NotEmpty @Value("\${tuturing.mapbox.token}") val mapboxToken: String
) {
    @Bean
    fun mapboxToken(): String {
        return mapboxToken
    }
}
