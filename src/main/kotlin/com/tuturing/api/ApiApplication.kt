package com.tuturing.api

import java.util.TimeZone
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationStartingEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Lazy

@Lazy(false)
@SpringBootApplication
class ApiApplication {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Runs when this "bean" is initialized. Order of bean instantiation is unknown and not guaranteed.
     */
    @PostConstruct
    fun startupApplication() {
        logger.info("Tuturing API Server")
    }
}

fun main(args: Array<String>) {
    configureEnvironment()
    runApplication<ApiApplication>(*args) {
        this.addListeners(
            ApplicationListener<ApplicationStartingEvent> {
                // this will run roughly before anything else, including the banner and it is blocking
                // you may not be able to log from here because the logger is not instantiated
            }
        )
    }
}

/**
 * Configures the JVM and envionment properties.
 */
private fun configureEnvironment() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    // some Sabre SOAP WSDL files reference HTTP resources
    // the default setting of webservices is to allow HTTPS only
    System.setProperty("javax.xml.accessExternalDTD", "all")
}
