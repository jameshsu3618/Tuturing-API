package com.tuturing.api.user.valueobject

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

data class UserRegistrationParams(
    val jwtPublicKey: RSAPublicKey,
    val jwtPrivateKey: RSAPrivateKey,
    val jwtIssuer: String,
    val invitationExpirationTime: Int,
    val appBaseUrl: String,
    val cdnBaseUrl: String,
    val userGuideUrl: String,
    val adminGuideUrl: String,
    val defaultEmailSender: String,
    val userVerificationTemplate: String,
    val userVerificationSubject: String,
    val userOnBoardingTemplate: String,
    val userOnBoardingSubject: String
)
