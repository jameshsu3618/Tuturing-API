package com.tuturing.api.shared.api.recaptcha.dto

data class RecaptchaResponseDto(
    var success: Boolean,
    var score: Double,
    var challenge_ts: String?,
    var hostname: String?,
    var `error-codes`: List<String>?
)
