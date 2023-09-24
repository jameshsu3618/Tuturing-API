package com.tuturing.api.user.dto.resource

import com.tuturing.api.loyalty.dto.HotelChainDto
import java.util.UUID

data class HotelChainLoyaltyProgramDto(
    var id: UUID?,
    var number: String,
    var hotelChain: HotelChainDto
) {
    constructor() : this(null, "", HotelChainDto())
}
