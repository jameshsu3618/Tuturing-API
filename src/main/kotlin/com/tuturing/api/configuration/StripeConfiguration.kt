package com.tuturing.api.configuration

import com.tuturing.api.paymentmethod.api.StripeClient
import com.stripe.net.RequestOptions
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StripeConfiguration(
    @NotEmpty @Value("\${tuturing.stripe.account-id}") var accountId: String,
    @NotEmpty @Value("\${tuturing.stripe.api-key}") var apiKey: String
) {

    val requestOptions: RequestOptions = RequestOptions.RequestOptionsBuilder()
        .setApiKey(apiKey)
        .setStripeAccount(accountId)
        .build()

    @Bean
    fun stripeClient(): StripeClient {
        return StripeClient(this.requestOptions)
    }
}
