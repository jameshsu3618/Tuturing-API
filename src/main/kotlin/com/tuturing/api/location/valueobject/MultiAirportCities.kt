package com.tuturing.api.location.valueobject

import com.squareup.moshi.Json

data class MultiAirportCities(
    @Json(name = "cities")
    var cities: List<MultiAirportCity>
)
