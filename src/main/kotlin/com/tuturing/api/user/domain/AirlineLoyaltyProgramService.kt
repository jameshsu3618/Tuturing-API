package com.tuturing.api.user.domain

import com.tuturing.api.user.entity.AirlineLoyaltyProgramEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.repository.AirlineLoyaltyProgramRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class AirlineLoyaltyProgramService(
    @Autowired private val airlineLoyaltyProgramRepository: AirlineLoyaltyProgramRepository
) {
    @PreAuthorize("#tuturing.isPersonal(#loyaltyProgram)")
    fun save(loyaltyProgram: AirlineLoyaltyProgramEntity) {
        airlineLoyaltyProgramRepository.save(loyaltyProgram)
    }

    @PostAuthorize("#tuturing.isPersonal(returnObject)")
    fun findById(id: UUID): AirlineLoyaltyProgramEntity? {
        return airlineLoyaltyProgramRepository.findByIdOrNull(id)
    }

    @PreAuthorize("#tuturing.isPersonal(#profile)")
    fun findAllByUserProfile(profile: UserProfileEntity): MutableIterable<AirlineLoyaltyProgramEntity> {
        return airlineLoyaltyProgramRepository.findByUserProfile(profile)
    }

    @PreAuthorize("#tuturing.isPersonal(#loyaltyProgram)")
    fun delete(loyaltyProgram: AirlineLoyaltyProgramEntity) {
        airlineLoyaltyProgramRepository.delete(loyaltyProgram)
    }
}
