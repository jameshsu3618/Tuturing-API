package com.tuturing.api.user.repository

import com.tuturing.api.user.entity.TravelDocumentEntity
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface TravelDocumentRepository : CrudRepository<TravelDocumentEntity, UUID> {
    fun findByUserProfileId(id: UUID?): MutableIterable<TravelDocumentEntity>
}
