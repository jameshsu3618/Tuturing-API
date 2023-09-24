package com.tuturing.api.user.dto.resource

import java.util.*

data class PhoneNumberDto(
    var id: UUID?,
    var number: String,
    var countryCode: String,
    var phoneNumberCountryCode: Int
) {
    constructor() : this(null, "", "", 0)
}
