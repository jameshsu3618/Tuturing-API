package com.tuturing.api.configuration

import com.tuturing.api.shared.api.sendgrid.SendgridApiClient
import javax.validation.constraints.NotEmpty
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SendgridConfiguration(
    @Autowired private val okHttpClient: OkHttpClient,
    @NotEmpty @Value("\${tuturing.sendgrid.host}") var host: String,
    @NotEmpty @Value("\${tuturing.sendgrid.apiVersion}") var apiVersion: String,
    @NotEmpty @Value("\${tuturing.sendgrid.api-key}") var apiKey: String
) {
    @Bean
    fun sendgrid(): SendgridApiClient {
        return SendgridApiClient(host, apiVersion, apiKey, okHttpClient)
    }
}
