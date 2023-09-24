package com.tuturing.api.user.dto.resource.admin

import com.tuturing.api.user.valueobject.Role
import java.util.UUID

data class UserUpdateDto(
    var id: UUID?,
    var role: Role,
    var email: String,
    var profile: UserProfileDto,
    var policy: UserPolicyDto?
) {
    constructor() : this(null, Role.EMPLOYEE, "", UserProfileDto(), null)
}
