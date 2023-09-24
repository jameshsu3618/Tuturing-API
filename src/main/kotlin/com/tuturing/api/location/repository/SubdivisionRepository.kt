package com.tuturing.api.location.repository

import com.tuturing.api.location.entity.SubdivisionEntity
import java.util.UUID
import org.springframework.data.repository.CrudRepository

interface SubdivisionRepository : CrudRepository<SubdivisionEntity, UUID> {
    fun findByIsoSubdivisionCode(countryCode: String?): SubdivisionEntity?
    fun findAllByCountryId(ID: UUID?): List<SubdivisionEntity>?
}
