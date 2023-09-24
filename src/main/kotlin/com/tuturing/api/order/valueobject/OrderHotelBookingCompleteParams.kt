package com.tuturing.api.order.valueobject

data class OrderHotelBookingCompleteParams(
    val orderHotelCompleteSender: String,
    val orderHotelCompleteSubject: String,
    val orderHotelCompleteTemplate: String,
    val cdnBaseUrl: String,
    val hotelBookingProvider: String
)
