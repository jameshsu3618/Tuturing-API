package com.tuturing.api.shared.dto.error

data class Error(
    val reason: String?,
    val message: String?,
    val invalidFields: List<FieldError>? = null,
    val body: Any? = null
)
