package com.tuturing.api.user.valueobject

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

data class PasswordServiceParams(
    val minPasswordLength: Int,
    val jwtPublicKey: RSAPublicKey,
    val jwtPrivateKey: RSAPrivateKey,
    val jwtIssuer: String,
    val passwordResetExpirationTime: Int,
    val appBaseUrl: String,
    val cdnBaseUrl: String,
    val defaultEmailSender: String,
    val passwordChangeTemplate: String,
    val passwordChangeSubject: String,
    val passwordResetTriggerTemplate: String,
    val passwordResetSuccessTemplate: String,
    val passwordResetSubject: String
)
