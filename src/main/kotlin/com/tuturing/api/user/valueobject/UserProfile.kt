package com.tuturing.api.user.valueobject

data class UserProfile(
    var firstName: String?,
    var lastName: String?
) {
    constructor() : this("", "")
}
