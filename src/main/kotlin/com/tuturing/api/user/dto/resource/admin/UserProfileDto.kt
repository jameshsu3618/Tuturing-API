package com.tuturing.api.user.dto.resource.admin

import java.util.UUID

data class UserProfileDto(
    var id: UUID?,
    var firstName: String,
    var lastName: String
) {
    // Necessary for MapStruct
    constructor() : this(null, "", "")
}
