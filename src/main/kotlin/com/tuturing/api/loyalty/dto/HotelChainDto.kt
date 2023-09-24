package com.tuturing.api.loyalty.dto

import java.util.UUID

data class HotelChainDto(
    var id: UUID?,
    var fullName: String,
    var shortName: String?,
    var loyaltyProgramName: String?
) {
    // Necessary for MapStruct
    constructor() : this(null, "", null, null)
}
