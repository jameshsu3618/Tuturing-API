package com.tuturing.api.order.valueobject

data class Charge(
    val amountBase: String,
    val amountTax: String,
    val amountTotal: String,
    val paymentCard: String
)
