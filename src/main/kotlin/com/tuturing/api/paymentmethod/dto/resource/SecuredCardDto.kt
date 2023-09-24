package com.tuturing.api.paymentmethod.dto.resource

data class SecuredCardDto(
    var cardNumber: String,
    var cvv: String,
    var expirationMonth: Long,
    var expirationYear: Long
) {
    constructor() : this("", "0", 0, 0)
}
