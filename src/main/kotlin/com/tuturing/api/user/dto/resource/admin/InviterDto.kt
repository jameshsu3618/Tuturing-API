package com.tuturing.api.user.dto.resource.admin

import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserStatus
import java.util.*

data class InviterDto(
    var id: UUID?,
    var email: String,
    var role: Role,
    var status: UserStatus,
    var profile: UserProfileDto
) {
    constructor() : this(null, "", Role.EMPLOYEE, UserStatus.CREATED, UserProfileDto())
}
