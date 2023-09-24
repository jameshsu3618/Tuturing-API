package com.tuturing.api.security.service.expression

import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.entity.BaseEntity
import com.tuturing.api.shared.entity.PersonalEntity
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.valueobject.Role.Companion.adminRoles
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication

class TuturingSecurityExpressionMethods(val authentication: Authentication) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun isPrincipal(entity: BaseEntity): Boolean {
        logger.debug("Executing #tuturing.isPrincipal on {}", entity)

        if (!authentication.isAuthenticated) return false

        val principal = authentication.principal!!

        return (principal is CustomUserDetails) && when (entity) {
            is UserEntity -> entity.id == principal.user.id
            is UserProfileEntity -> entity.id == principal.user.profile.id
            else -> false
        }
    }

    fun isAnAdmin(): Boolean {
        logger.debug("Executing #tuturing.isAnAdmin")

        if (!authentication.isAuthenticated) return false

        val principal = authentication.principal!!

        return (principal is CustomUserDetails) && principal.user.role in adminRoles
    }


    fun isPersonal(entity: BaseEntity?): Boolean {
        logger.debug("Executing #tuturing.isPersonal on {}", entity)

        if (!authentication.isAuthenticated) return false

        if (null == entity) return true // nothing to authorize

        val principal = authentication.principal!!

        return (principal is CustomUserDetails) && when (entity) {
            is UserEntity -> entity.id == principal.user.id
            is UserProfileEntity -> entity.id == principal.user.profile.id
            is PersonalEntity -> entity.userProfile.id == principal.user.profile.id
            else -> false
        }
    }
}
