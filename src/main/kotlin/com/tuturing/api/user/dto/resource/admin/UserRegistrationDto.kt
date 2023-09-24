package com.tuturing.api.user.dto.resource.admin

import com.tuturing.api.user.valueobject.Role

data class UserRegistrationDto(
    var email: String,
    var role: Role,
    var profile: UserProfileDto
) {
    constructor() : this("", Role.EMPLOYEE, UserProfileDto())
}
