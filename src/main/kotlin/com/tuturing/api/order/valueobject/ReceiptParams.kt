package com.tuturing.api.order.valueobject

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

data class ReceiptParams(
    val apiBaseUrl: String,
    val jwtPublicKey: RSAPublicKey,
    val jwtPrivateKey: RSAPrivateKey,
    val jwtIssuer: String,
    val receiptExpirationTime: Int
)
