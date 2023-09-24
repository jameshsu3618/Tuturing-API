package com.tuturing.api.order.valueobject

class FlightReceiptSegment(
    val is_return: Boolean,
    val destination: FlightReceiptAirport?,
    val flight_legs: List<FlightReceiptLeg>,
    val total_duration: String
)
