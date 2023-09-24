package com.tuturing.api.user.valueobject

enum class UserStatus {
    CREATED, // created but not invited
    INVITED, // invitation sent and need to be accepted/verified
    ACTIVATED, // fully activated using invite link (clicked on link, set the password)
    DEACTIVATED, // deactivated by company admin and has been activated before
    CANCELED // deactivated by company admin before activating
}
