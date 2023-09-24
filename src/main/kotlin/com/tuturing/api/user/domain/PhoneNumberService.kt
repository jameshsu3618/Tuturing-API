package com.tuturing.api.user.domain

import com.tuturing.api.user.entity.PhoneNumberEntity
import com.tuturing.api.user.entity.UserProfileEntity
import com.tuturing.api.user.repository.PhoneNumberRepository
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhoneNumberService(
    @Autowired private val phoneNumberRepository: PhoneNumberRepository
) {
    @PreAuthorize("#tuturing.isPersonal(#phoneNumber)")
    @Transactional
    fun save(phoneNumber: PhoneNumberEntity) {
        phoneNumberRepository.save(phoneNumber)
    }

    @PostAuthorize("#tuturing.isPersonal(returnObject)")
    fun findById(id: UUID): PhoneNumberEntity? {
        return phoneNumberRepository.findByIdOrNull(id)
    }

    @PreAuthorize("#tuturing.isPrincipal(#userProfile)")
    fun findAllById(userProfile: UserProfileEntity): MutableIterable<PhoneNumberEntity> {
        return phoneNumberRepository.findByUserProfileId(userProfile.id)
    }

    @PreAuthorize("#tuturing.isPersonal(#phoneNumber)")
    fun delete(phoneNumber: PhoneNumberEntity) {
        phoneNumberRepository.delete(phoneNumber)
    }
}
