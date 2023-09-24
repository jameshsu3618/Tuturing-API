package com.tuturing.api.location.valueobject

import com.tuturing.api.shared.valueobject.Distance
import java.math.BigDecimal

data class Airport(
    val iataCode: String?,
    val name: String?,
    val city: String?,
    val region: String?,
    val country: String?,
    val latitude: BigDecimal?,
    val longitude: BigDecimal?,
    val distance: Distance?,
    var nearbyAirports: List<Airport>?,
    var type: LocationType?
)
