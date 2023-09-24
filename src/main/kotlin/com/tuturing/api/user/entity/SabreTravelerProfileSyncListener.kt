package com.tuturing.api.user.entity

import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.shared.jms.TravelerProfileProducer
import java.util.*
import javax.persistence.PostPersist
import javax.persistence.PostUpdate
import org.springframework.beans.factory.annotation.Autowired

class SabreTravelerProfileSyncListener(
    @Autowired private val producer: TravelerProfileProducer
) {
    @PostPersist
    fun onCreate(any: Any) {
        when (any) {
            is UserProfileEntity -> enqueueCreate(any.id)
            is UserEntity -> {} // don't do anything yet, wait for the user profile to be created first
            else -> onUpdate(any)
        }
    }

    @PostUpdate
    fun onUpdate(any: Any) {
        when (any) {
            is UserProfileEntity -> enqueueUpdate(any.id)
            is UserEntity -> enqueueUpdate(any.profile.id)
            is TravelDocumentEntity -> enqueueUpdate(any.userProfile.id)
            is PhoneNumberEntity -> enqueueUpdate(any.userProfile.id)
            is PersonalCardEntity -> enqueueUpdate(any.userProfile.id)
            is AirlineLoyaltyProgramEntity -> enqueueUpdate(any.userProfile.id)
        }
    }

    private fun enqueueCreate(id: UUID?) {
        id?.let { producer.travelerProfileCreate(it) }
    }

    private fun enqueueUpdate(id: UUID?) {
        id?.let { producer.travelerProfileUpdate(it) }
    }
}
