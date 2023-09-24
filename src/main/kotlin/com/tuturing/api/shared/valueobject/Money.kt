package com.tuturing.api.shared.valueobject

import java.math.BigDecimal

data class Money(
    var amount: BigDecimal,
    var currency: String
) {
    constructor() : this(0.toBigDecimal(), "")

    override fun equals(other: Any?): Boolean {
        return if (other is Money) {
            other.amount.compareTo(amount) == 0 && other.currency == currency
        } else {
            false
        }
    }
}
