package com.tuturing.api.user.dto.command

data class ResetPasswordDto(
    val password: String,
    val token: String
)
