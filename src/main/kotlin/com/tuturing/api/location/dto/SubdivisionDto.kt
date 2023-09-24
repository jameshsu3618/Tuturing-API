package com.tuturing.api.location.dto

import java.util.UUID

data class SubdivisionDto(
    var id: UUID?,
    var countryCode: String,
    var fullName: String,
    var isoSubdivisionCode: String?
) {
    // Necessary for MapStruct
    constructor() : this(null, "", "", null)
}
