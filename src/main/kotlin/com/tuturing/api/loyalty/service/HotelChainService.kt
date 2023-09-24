package com.tuturing.api.loyalty.service

import com.tuturing.api.loyalty.entity.HotelChainEntity
import com.tuturing.api.loyalty.repository.HotelChainRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class HotelChainService(
    @Autowired private val hotelChainRepository: HotelChainRepository
) {
    fun findById(id: UUID): HotelChainEntity? {
        return hotelChainRepository.findByIdOrNull(id)
    }

    fun findAllHavingLoyaltyProgram(): MutableIterable<HotelChainEntity> {
        return hotelChainRepository.findByLoyaltyProgramNameNotNull()
    }
}
