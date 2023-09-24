package com.tuturing.api.location.valueobject

import com.fasterxml.jackson.annotation.JsonProperty

data class JsonCallingCode(
    val name: String,
    @JsonProperty("dial_code")
    val dialCode: String,
    val code: String,
    val latitude: Double,
    val longitude: Double
) {
    constructor() : this("", "", "", 0.0, 0.0)
}
