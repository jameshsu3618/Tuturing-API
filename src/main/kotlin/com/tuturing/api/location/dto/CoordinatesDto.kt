package com.tuturing.api.location.dto

data class CoordinatesDto(
    var lon: Double,
    var lat: Double
) {
    constructor() : this(0.0, 0.0)
}
