package com.tuturing.api.loyalty.repository

import com.tuturing.api.loyalty.entity.HotelChainEntity
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface HotelChainRepository : CrudRepository<HotelChainEntity, UUID> {
    fun findByLoyaltyProgramNameNotNull(): MutableIterable<HotelChainEntity>
}
