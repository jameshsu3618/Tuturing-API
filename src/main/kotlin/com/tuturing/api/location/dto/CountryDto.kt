package com.tuturing.api.location.dto

import java.util.*

data class CountryDto(
    var id: UUID?,
    var fullName: String,
    var shortName: String,
    var isoCodeAlpha2: String,
    var isoCodeAlpha3: String,
    var phoneNumberCountryCode: Int?,
    var subdivisions: List<SubdivisionDto>?
) {
    // Necessary for MapStruct
    constructor() : this(null, "", "", "", "", 1, listOf<SubdivisionDto>())
}
