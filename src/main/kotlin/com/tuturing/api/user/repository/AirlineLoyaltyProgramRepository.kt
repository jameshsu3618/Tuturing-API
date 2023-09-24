package com.tuturing.api.user.repository

import com.tuturing.api.user.entity.AirlineLoyaltyProgramEntity
import com.tuturing.api.user.entity.UserProfileEntity
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface AirlineLoyaltyProgramRepository : CrudRepository<AirlineLoyaltyProgramEntity, UUID> {
    fun findByUserProfile(userProfile: UserProfileEntity): MutableIterable<AirlineLoyaltyProgramEntity>
}
