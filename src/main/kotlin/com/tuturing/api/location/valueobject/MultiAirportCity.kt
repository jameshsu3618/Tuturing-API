package com.tuturing.api.location.valueobject

data class MultiAirportCity(
    var iataCode: String,
    var name: String,
    var countryCode: String,
    var countryName: String,
    var regionName: String,
    var airports: List<Airport>?
)
