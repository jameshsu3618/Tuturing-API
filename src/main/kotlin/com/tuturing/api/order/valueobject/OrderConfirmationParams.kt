package com.tuturing.api.order.valueobject

data class OrderConfirmationParams(
    val orderConfirmationSender: String,
    val orderConfirmationSubject: String,
    val orderConfirmationTemplate: String,
    val cdnBaseUrl: String
)
