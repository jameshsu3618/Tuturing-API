package com.tuturing.api.configuration

import javax.servlet.Filter
import javax.validation.constraints.NotEmpty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfiguration(
    @NotEmpty @Value("\${tuturing.security.cors.allow-credentials:true}") val allowCredentials: Boolean,
    @NotEmpty @Value("\${tuturing.security.cors.allowed-origins:*}") val allowedOrigins: String,
    @NotEmpty @Value("\${tuturing.security.cors.allow-headers:*}") val allowedHeaders: String,
    @NotEmpty @Value("\${tuturing.security.cors.allow-methods:*}") val allowedMethods: String,
    @NotEmpty @Value("\${tuturing.security.cors.exposed-headers:content-length}") val exposedHeaders: String,
    @NotEmpty @Value("\${tuturing.security.cors.max-age:3600}") val maxAge: Long,
    @NotEmpty @Value("\${tuturing.security.cors.path:/**}") val path: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun corsFilterRegistrationBean(): FilterRegistrationBean<*> {
        logger.debug("Configuring CORS")
        logger.debug("CORS allow credentials: {}", allowCredentials)
        logger.debug("CORS allowed origins: {}", allowedOrigins)
        logger.debug("CORS allowed headers: {}", allowedHeaders)
        logger.debug("CORS allowed methods: {}", allowedMethods)
        logger.debug("CORS exposed headers: {}", exposedHeaders)
        logger.debug("CORS max age: {}", maxAge)
        logger.debug("CORS path: {}", path)

        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.applyPermitDefaultValues()
        config.allowCredentials = allowCredentials
        config.allowedOrigins = listOf(allowedOrigins)
        config.allowedHeaders = listOf(allowedHeaders)
        config.allowedMethods = listOf(allowedMethods)
        config.exposedHeaders = listOf(exposedHeaders)
        config.maxAge = maxAge
        source.registerCorsConfiguration(path, config)
        val bean: FilterRegistrationBean<*> = FilterRegistrationBean<Filter>(CorsFilter(source))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }
}
