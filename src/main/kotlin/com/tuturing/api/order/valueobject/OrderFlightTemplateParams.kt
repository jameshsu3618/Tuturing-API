package com.tuturing.api.order.valueobject

data class OrderFlightTemplateParams(
    val orderFlightSender: String,
    val orderFlightSubjectComplete: String,
    val orderFlightSubjectModification: String,
    val orderFlightTemplate: String,
    val orderFlightTemplatePdf: String,
    val cdnBaseUrl: String
)
