package com.tuturing.api.location.repository

import com.tuturing.api.location.entity.AirportEntity
import java.util.*
import org.springframework.data.repository.CrudRepository

interface AirportRepository : CrudRepository<AirportEntity, UUID> {
    fun findByIataCode(iataCode: String): AirportEntity?
}
