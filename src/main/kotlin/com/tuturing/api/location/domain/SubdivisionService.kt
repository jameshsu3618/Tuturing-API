package com.tuturing.api.location.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.tuturing.api.location.entity.CountryEntity
import com.tuturing.api.location.entity.SubdivisionEntity
import com.tuturing.api.location.repository.CountryRepository
import com.tuturing.api.location.repository.SubdivisionRepository
import java.io.BufferedReader
import java.util.*
import kotlin.collections.ArrayList
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubdivisionService(
    @Autowired private val subdivisionRepository: SubdivisionRepository,
    @Autowired private val countryRepository: CountryRepository,
    @Value("classpath:json/subdivision-all.json") private val subdivisionJsonResource: Resource
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findBySubdivisionCode(subdivisionCode: String?): SubdivisionEntity? {
        return subdivisionRepository.findByIsoSubdivisionCode(subdivisionCode)
    }

    fun findAllByCountry(id: UUID?): List<SubdivisionEntity>? {
        return subdivisionRepository.findAllByCountryId(id)
    }

    fun findOrRetrieveListOfSubdivisions(country: CountryEntity): List<SubdivisionEntity>? {
        val subdivisions = country.subdivisions
        // check if country already has a list subdivisions saved
        return if (subdivisions.isNullOrEmpty()) {
            findByCountryCodeAndPopulateSubdivisionsDatabase(country)
        } else this.findAllByCountry(country.id)
    }

    @Transactional
    fun findByCountryCodeAndPopulateSubdivisionsDatabase(country: CountryEntity): List<SubdivisionEntity> {
        val jsonString = subdivisionJsonResource.inputStream.bufferedReader().use(BufferedReader::readText)
        val mapper = jacksonObjectMapper()

        val subdivisionList: MutableList<SubdivisionEntity> = ArrayList()

        val jsonTextList: List<SubdivisionEntity> = mapper.readValue<List<SubdivisionEntity>>(jsonString)

            for (sub in jsonTextList) {
                if (country.isoCodeAlpha2 == sub.countryCode) {
                    val subdivision = SubdivisionEntity()

                    subdivision.fullName = sub.fullName
                    subdivision.isoSubdivisionCode = sub.isoSubdivisionCode
                    subdivision.countryCode = sub.countryCode

                    subdivision.country = country
                    countryRepository.save(country)

                    subdivisionList.add(subdivision)
                    subdivisionRepository.saveAll(subdivisionList)
                }
            }
        return subdivisionList
    }
}
