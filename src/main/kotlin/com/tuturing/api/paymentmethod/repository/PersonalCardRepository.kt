package com.tuturing.api.paymentmethod.repository

import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.user.entity.UserProfileEntity
import java.util.*
import org.springframework.data.domain.Sort
import org.springframework.data.repository.CrudRepository

interface PersonalCardRepository : CrudRepository<PersonalCardEntity, UUID> {
    fun findAllByUserProfileIdAndIsDeleted(id: UUID, isDeleted: Boolean, sort: Sort): MutableIterable<PersonalCardEntity>

    fun findAllByUserProfileAndCardNumberOrderByExpirationDateDesc(userProfile: UserProfileEntity, cardNumber: String): List<PersonalCardEntity>
}
