package com.tuturing.api.order.configuration

import com.tuturing.api.order.valueobject.*
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OrderTemplateConfiguration(
    @NotEmpty @Value("\${tuturing.emails.default-sender}") val defaultEmailSender: String,
    @NotEmpty @Value("\${tuturing.emails.order-confirmation.subject}") val orderConfirmationSubject: String,
    @NotEmpty @Value("\${tuturing.emails.order-confirmation.template}") val orderConfirmationTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.order-flight.subject-confirmation}") val orderFlightSubjectConfirmation: String,
    @NotEmpty @Value("\${tuturing.emails.order-flight.subject-modification}") val orderFlightSubjectModification: String,
    @NotEmpty @Value("\${tuturing.emails.order-flight.template}") val orderFlightTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.order-flight.template-pdf}") val orderFlightPdfTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.order-hotel.subject-confirmation}") val orderHotelSubjectConfirmation: String,
    @NotEmpty @Value("\${tuturing.emails.order-hotel.subject-modification}") val orderHotelSubjectModification: String,
    @NotEmpty @Value("\${tuturing.emails.order-hotel.template}") val orderHotelEmailTemplate: String,
    @NotEmpty @Value("\${tuturing.emails.order-hotel.template-pdf}") val orderHotelPdfTemplate: String,
    @NotEmpty @Value("\${tuturing.cdn-base-url}") val cdnBaseUrl: String
) {
    @Bean
    fun orderConfirmationParams(): OrderConfirmationParams {
        return OrderConfirmationParams(
            defaultEmailSender,
            orderConfirmationSubject,
            orderConfirmationTemplate,
            cdnBaseUrl
        )
    }

    @Bean
    fun orderFlightTemplateParams(): OrderFlightTemplateParams {
        return OrderFlightTemplateParams(
            defaultEmailSender,
            orderFlightSubjectConfirmation,
            orderFlightSubjectModification,
            orderFlightTemplate,
            orderFlightPdfTemplate,
            cdnBaseUrl
        )
    }

    @Bean
    fun orderHotelEmailParams(): OrderHotelEmailParams {
        return OrderHotelEmailParams(
            defaultEmailSender,
            orderHotelSubjectConfirmation,
            orderHotelSubjectModification,
            orderHotelEmailTemplate,
            orderHotelPdfTemplate,
            cdnBaseUrl
        )
    }
}
