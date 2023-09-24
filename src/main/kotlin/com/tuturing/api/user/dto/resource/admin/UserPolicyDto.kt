package com.tuturing.api.user.dto.resource.admin

import java.util.UUID

data class UserPolicyDto(
    var id: UUID?,
    var name: String?,
    var description: String?
) {
    constructor() : this(null, null, null)
}
