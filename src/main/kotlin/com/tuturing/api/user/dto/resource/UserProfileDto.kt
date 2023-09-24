package com.tuturing.api.user.dto.resource

import com.tuturing.api.user.valueobject.Gender
import java.time.LocalDate
import java.util.UUID

data class UserProfileDto(
    var id: UUID?,
    var firstName: String,
    var middleName: String?,
    var lastName: String,
    var prefix: String?,
    var suffix: String?,
    var birthDate: LocalDate?,
    var gender: Gender?,
    var knownTravelerNumber: String?,
    var redressNumber: String?,
    var phoneNumbers: List<PhoneNumberDto>?
) {
    // Necessary for MapStruct
    constructor() : this(null, "", null, "", null, null, null, null, null, null, null)
}
