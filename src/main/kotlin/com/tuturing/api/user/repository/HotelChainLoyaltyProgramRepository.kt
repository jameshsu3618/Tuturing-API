package com.tuturing.api.user.repository

import com.tuturing.api.user.entity.HotelChainLoyaltyProgramEntity
import com.tuturing.api.user.entity.UserProfileEntity
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface HotelChainLoyaltyProgramRepository : CrudRepository<HotelChainLoyaltyProgramEntity, UUID> {
    fun findByUserProfile(userProfile: UserProfileEntity): MutableIterable<HotelChainLoyaltyProgramEntity>
}
