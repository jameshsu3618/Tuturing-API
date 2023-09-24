package com.tuturing.api.order.valueobject

data class Cancellation(
    val cancelledOn: String,
    val cancelledBy: String,
    val amountPenalty: String,
    val amountRefund: String
)
