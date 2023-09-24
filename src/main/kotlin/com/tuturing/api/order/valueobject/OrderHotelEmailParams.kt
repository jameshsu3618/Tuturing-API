package com.tuturing.api.order.valueobject

data class OrderHotelEmailParams(
    val sender: String,
    val subjectConfirmation: String,
    val subjectModification: String,
    val emailTemplate: String,
    val pdfTemplate: String,
    val cdnBaseUrl: String
)
