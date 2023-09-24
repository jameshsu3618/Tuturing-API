package com.tuturing.api.user.dto.resource

import com.tuturing.api.loyalty.dto.AirlineDto
import java.util.UUID

data class AirlineLoyaltyProgramDto(
    var id: UUID?,
    var number: String,
    var airline: AirlineDto
) {
    constructor() : this(null, "", AirlineDto())
}
