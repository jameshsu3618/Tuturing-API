package com.tuturing.api.location.valueobject

data class Coordinates(
    var lon: Double,
    var lat: Double
) {
    constructor() : this(0.0, 0.0)
}
