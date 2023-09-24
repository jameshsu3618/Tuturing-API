package com.tuturing.api.configuration

import com.tuturing.api.shared.controller.filter.MaintenanceFilter
import javax.servlet.Filter
import javax.validation.constraints.NotEmpty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration
class MaintenanceConfiguration(
    @NotEmpty @Value("\${tuturing.maintenance.enabled:false}") val isEnabled: Boolean
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun maintenanceFilter(): FilterRegistrationBean<*> {
        logger.debug("Configuring maintenance mode")
        logger.debug("Maintenance enabled: {}", isEnabled)

        val bean: FilterRegistrationBean<*> = FilterRegistrationBean<Filter>(MaintenanceFilter(isEnabled))
        bean.order = Ordered.HIGHEST_PRECEDENCE
        return bean
    }
}
