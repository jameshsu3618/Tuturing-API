package com.tuturing.api.loyalty.service

import com.tuturing.api.loyalty.entity.AirlineEntity
import com.tuturing.api.loyalty.repository.AirlineRepository
import java.util.UUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AirlineService(
    @Autowired private val airlineRepository: AirlineRepository
) {
    fun findById(id: UUID): AirlineEntity? {
        return airlineRepository.findByIdOrNull(id)
    }

    fun findByIataCode(iataCode: String): AirlineEntity? {
        return airlineRepository.findByIataCode(iataCode)
    }

    fun findAirlineNameByIataCode(iataCode: String): String? {
        return findByIataCode(iataCode)?.shortName
    }

    fun findAllHavingLoyaltyProgram(): MutableIterable<AirlineEntity> {
        return airlineRepository.findByLoyaltyProgramNameNotNull()
    }
}
