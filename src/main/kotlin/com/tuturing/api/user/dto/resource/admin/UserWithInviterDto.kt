package com.tuturing.api.user.dto.resource.admin

import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserStatus
import java.time.LocalDateTime
import java.util.UUID

data class UserWithInviterDto(
    var id: UUID?,
    var email: String,
    var role: Role,
    var status: UserStatus,
    var profile: UserProfileDto,
    var inviter: InviterDto?,
    var invitedAt: LocalDateTime?
) {
    constructor() : this(null, "", Role.EMPLOYEE, UserStatus.CREATED, UserProfileDto(), null, null)
}
