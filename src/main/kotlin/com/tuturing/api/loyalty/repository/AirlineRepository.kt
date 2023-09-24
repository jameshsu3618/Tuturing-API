package com.tuturing.api.loyalty.repository

import com.tuturing.api.loyalty.entity.AirlineEntity
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface AirlineRepository : CrudRepository<AirlineEntity, UUID> {
    fun findByLoyaltyProgramNameNotNull(): MutableIterable<AirlineEntity>
    fun findByIataCode(iataCode: String): AirlineEntity?
}
