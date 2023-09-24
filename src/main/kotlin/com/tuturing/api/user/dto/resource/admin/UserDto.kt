package com.tuturing.api.user.dto.resource.admin

import com.tuturing.api.policy.dto.PolicyDto
import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserStatus
import java.util.UUID

data class UserDto(
    var id: UUID?,
    var email: String,
    var role: Role,
    var status: UserStatus,
    var profile: UserProfileDto,
    var policy: PolicyDto?
) {
    constructor() : this(null, "", Role.EMPLOYEE, UserStatus.CREATED, UserProfileDto(), null)
}
