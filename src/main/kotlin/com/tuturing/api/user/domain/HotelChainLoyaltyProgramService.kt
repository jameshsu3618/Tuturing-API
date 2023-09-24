package com.tuturing.api.user.domain

import com.tuturing.api.user.entity.HotelChainLoyaltyProgramEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.repository.HotelChainLoyaltyProgramRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class HotelChainLoyaltyProgramService(
    @Autowired private val hotelChainLoyaltyProgramRepository: HotelChainLoyaltyProgramRepository
) {
    @PreAuthorize("#tuturing.isPersonal(#loyaltyProgram)")
    fun save(loyaltyProgram: HotelChainLoyaltyProgramEntity) {
        hotelChainLoyaltyProgramRepository.save(loyaltyProgram)
    }

    @PostAuthorize("#tuturing.isPersonal(returnObject)")
    fun findById(id: UUID): HotelChainLoyaltyProgramEntity? {
        return hotelChainLoyaltyProgramRepository.findByIdOrNull(id)
    }

    @PreAuthorize("#tuturing.isPersonal(#profile)")
    fun findByUserProfile(profile: UserProfileEntity): MutableIterable<HotelChainLoyaltyProgramEntity> {
        return hotelChainLoyaltyProgramRepository.findByUserProfile(profile)
    }

    @PreAuthorize("#tuturing.isPersonal(#loyaltyProgram)")
    fun delete(loyaltyProgram: HotelChainLoyaltyProgramEntity) {
        hotelChainLoyaltyProgramRepository.delete(loyaltyProgram)
    }
}
