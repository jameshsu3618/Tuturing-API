package com.tuturing.api.paymentmethod.dto.resource

import com.tuturing.api.paymentmethod.valueobject.CardNetwork
import com.tuturing.api.paymentmethod.valueobject.PaymentCardType
import java.util.*

data class PaymentCardDto(
    var id: UUID?,
    var cardNickname: String?,
    var nameOnCard: String,
    var cardNetwork: CardNetwork?,
    var paymentCardType: PaymentCardType?,
    var secureCardDetails: SecuredCardDto,
    var billingAddress: PaymentCardAddressDto
) {
    constructor() : this(UUID(0, 0), null, "", CardNetwork.UNKNOWN, PaymentCardType.PERSONAL_CREDIT_CARD, SecuredCardDto(), PaymentCardAddressDto())
}
