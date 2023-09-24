package com.tuturing.api.user.valueobject

import com.tuturing.api.policy.entity.PolicyEntity
import java.util.UUID

data class UserUpdateParams(
    var id: UUID?,
    var role: Role,
    var email: String,
    var profile: UserProfile,
    var policy: PolicyEntity?
) {
    constructor() : this(null, Role.EMPLOYEE, "", UserProfile(), null)
}
