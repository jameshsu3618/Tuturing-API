package com.tuturing.api.user.repository

import com.tuturing.api.shared.repository.CustomRepository
import com.tuturing.api.user.entity.UserProfileEntity
import java.util.UUID

interface UserProfileRepository : CustomRepository<UserProfileEntity, UUID> {
    fun findByUserId(userId: UUID): UserProfileEntity?

    fun save(profile: UserProfileEntity): UserProfileEntity?
}
