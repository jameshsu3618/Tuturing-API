package com.tuturing.api.location.repository

import com.tuturing.api.location.entity.CountryEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface CountryRepository : JpaRepository<CountryEntity, UUID> {
    fun findByIsoCodeAlpha2(countryCode: String): CountryEntity?
}
