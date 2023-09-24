package com.tuturing.api.order.dto

import com.tuturing.api.shared.valueobject.Money

data class CancellationDetailsDto(
    var refundAmount: Money,
    var penaltyAmount: Money,
    var cardLastFour: String?,
    var cardType: String?
) {
    constructor() : this(Money(0.toBigDecimal(), ""), Money(0.toBigDecimal(), ""), "", "")
}
