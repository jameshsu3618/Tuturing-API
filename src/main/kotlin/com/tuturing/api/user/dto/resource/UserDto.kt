package com.tuturing.api.user.dto.resource

import com.tuturing.api.company.dto.resource.CompanyDto
import com.tuturing.api.user.valueobject.Role
import java.util.UUID

data class UserDto(
    var id: UUID?,
    var email: String,
    var role: Role,
    var profile: UserProfileDto,
    var company: CompanyDto
) {
    constructor() : this(null, "", Role.EMPLOYEE, UserProfileDto(), CompanyDto())
}
