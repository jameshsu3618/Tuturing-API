package com.tuturing.api.user.repository

import com.tuturing.api.user.entity.PhoneNumberEntity
import java.util.*
import org.springframework.data.repository.CrudRepository

interface PhoneNumberRepository : CrudRepository<PhoneNumberEntity, UUID> {
    fun findByUserProfileId(id: UUID?): MutableIterable<PhoneNumberEntity>
}
