package com.tuturing.api.user.domain.exception

sealed class UserVerificationException(val reason: String, message: String) : Throwable(message) {
    object TokenInvalid : UserVerificationException("INVALID_TOKEN", "Token is invalid")
    object InvalidPassword : UserVerificationException("INVALID_PASSWORD", "The Password is invalid")
    object AlreadyVerified : UserVerificationException("ALREADY_VERIFIED", "User already verified")
}
