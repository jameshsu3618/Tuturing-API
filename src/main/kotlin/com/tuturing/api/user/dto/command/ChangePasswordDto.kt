package com.tuturing.api.user.dto.command

data class ChangePasswordDto(
    val oldPassword: String,
    val newPassword: String
)
