package com.tuturing.api.security

import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.valueobject.UserStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * CustomUserDetails is an implementation of Spring UserDetails interface.
 *
 * This is the class that is returned when we getPrincipal
 */
class CustomUserDetails(
    val user: UserEntity,
    val credentialsNonExpired: Boolean = true,
    val accountNonExpired: Boolean = true,
    val accountNonLocked: Boolean = true
) : UserDetails {
    private val authorities = listOf(SimpleGrantedAuthority("ROLE_" + user.role.toString()))

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun isEnabled(): Boolean {
        return user.status == UserStatus.ACTIVATED
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun getPassword(): String {
        return user.password ?: ""
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }
}
