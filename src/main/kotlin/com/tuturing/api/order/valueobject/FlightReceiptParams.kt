package com.tuturing.api.order.valueobject

data class FlightReceiptParams(
    val first_name: String?,
    val flight_segments: List<FlightReceiptSegment>,
    val trip_id: String?,
    val order_number: String?,
    val confirmation_number: String?,
    val booked_on: String?,
    val booker_name: String?,
    val traveler_name: String?,
    val policy_name: String?,
    val amount_base: String?,
    val amount_tax: String?,
    val amount_total: String?,
    val payment_card: String?,
    val payment_card_network: String?,
    val cdn_url: String?,
    val flight_information: String?
)
