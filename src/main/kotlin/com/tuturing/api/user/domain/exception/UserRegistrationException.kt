package com.tuturing.api.user.domain.exception

import com.tuturing.api.user.valueobject.Role

sealed class UserRegistrationException(val reason: String, message: String) : Throwable(message) {
    class RoleNotAllowed(role: Role) :
        UserRegistrationException("ROLE_NOT_ALLOWED", "The role ${role.name} is not allowed")

    class AlreadyRegistered(email: String) :
        UserRegistrationException("ALREADY_REGISTERED", "The user with email $email is already registered")
}
