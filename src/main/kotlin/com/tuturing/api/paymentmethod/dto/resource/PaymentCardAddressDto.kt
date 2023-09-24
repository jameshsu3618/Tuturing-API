package com.tuturing.api.paymentmethod.dto.resource

import java.util.*

data class PaymentCardAddressDto(
    var id: UUID?,
    var addressOne: String,
    var addressTwo: String?,
    var city: String,
    var subdivisionCode: String,
    var countryCode: String,
    var zipCode: String
) {
    constructor() : this(null, "", "", "", "", "", "")
}
