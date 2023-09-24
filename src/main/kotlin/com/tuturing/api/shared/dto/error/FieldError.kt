package com.tuturing.api.shared.dto.error

data class FieldError(
    val reason: String,
    val message: String,
    val field: String
)
