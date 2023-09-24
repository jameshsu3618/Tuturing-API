package com.tuturing.api.user.dto.command

data class VerifyDto(
    var token: String,
    var password: String,
    var firstName: String,
    var lastName: String
)
