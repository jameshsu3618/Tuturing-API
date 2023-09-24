package com.tuturing.api.shared.service

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthenticationFacade {
    val hasSecurityContext: Boolean
        get() = null != SecurityContextHolder.getContext()

    val hasAuthentication: Boolean
        get() = hasSecurityContext && null != SecurityContextHolder.getContext().authentication

    val authentication: Authentication
        get() = SecurityContextHolder.getContext().authentication
}
