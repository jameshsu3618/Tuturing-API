package com.tuturing.api.order.valueobject

data class FlightReceiptLeg(
    val departure_date: String?,
    val departure_time: String?,
    val arrival_date: String?,
    val arrival_time: String?,
    val is_next_day_arrival: Boolean,
    val is_next_day_departure: Boolean,
    val departure_airport: FlightReceiptAirport?,
    val arrival_airport: FlightReceiptAirport?,
    val airline_marketing_name: String?,
    val airline_marketing_iata: String?,
    val airline_operating_name: String?,
    val airline_operating_iata: String?,
    val airline_operating_flight_number: String?,
    val flight_duration: String?,
    val is_layover: Boolean,
    val layover_duration: String,
    val cabin_name: String?,
    val booking_code: String?
)
