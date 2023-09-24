package com.tuturing.api.paymentmethod.dto.resource

data class PaymentCardsDto(
    var paymentCardDto: PaymentCardDto,
    var expired: Boolean
) {
    constructor() : this (PaymentCardDto(), false)
}
