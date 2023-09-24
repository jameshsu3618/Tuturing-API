package com.tuturing.api.loyalty.dto

import java.util.UUID

data class AirlineDto(
    var id: UUID?,
    var fullName: String,
    var shortName: String?,
    var iataCode: String?,
    var loyaltyProgramName: String?

) {
    // Necessary for MapStruct
    constructor() : this(null, "", null, null, null)
}
