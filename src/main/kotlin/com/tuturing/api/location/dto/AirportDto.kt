package com.tuturing.api.location.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.tuturing.api.location.valueobject.LocationType
import com.tuturing.api.shared.valueobject.Distance
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AirportDto(
    var iataCode: String,
    var name: String?,
    var city: String?,
    var region: String?,
    var country: String?,
    var latitude: BigDecimal?,
    var longitude: BigDecimal?,
    var distance: Distance?,
    var nearbyAirports: List<AirportDto>?,
    var type: LocationType?
) {
    constructor() : this("", null, null, null, null, null, null, null, null, null)
}
