package com.tuturing.api.configuration

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OkHttpConfiguration(
    @Autowired val registry: MeterRegistry
) {
    @Bean
    fun okHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        logging.redactHeader("Authorization")

        return OkHttpClient.Builder()
            .eventListener(
                OkHttpMetricsEventListener.builder(registry, "okhttp.requests")
                    .uriMapper { req -> req.url().encodedPath() }
                    .build()
            )
            .addInterceptor(logging)
            .build()
    }
}
