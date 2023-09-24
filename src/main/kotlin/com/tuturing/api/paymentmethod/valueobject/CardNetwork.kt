package com.tuturing.api.paymentmethod.valueobject

/**
 * Card brand according to Stripe docs can be {@code amex}, {@code diners}, {@code discover}, {@code jcb}, {@code
 * mastercard}, {@code unionpay}, {@code visa}, or {@code unknown}.
 */

enum class CardNetwork(val network: String) {
    AMEX("amex"),
    DINERS("diners"),
    DISCOVER("discover"),
    JCB("jcb"),
    MASTERCARD("mastercard"),
    UNIONPAY("unionpay"),
    VISA("visa"),
    UNKNOWN("unknown")
}
