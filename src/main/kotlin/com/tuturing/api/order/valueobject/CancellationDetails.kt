package com.tuturing.api.order.valueobject

import com.tuturing.api.shared.valueobject.Money

data class CancellationDetails(
    val refundAmount: Money,
    val penaltyAmount: Money,
    val cardLastFour: String?,
    val cardType: String?
)
