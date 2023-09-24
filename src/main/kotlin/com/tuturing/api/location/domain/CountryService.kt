package com.tuturing.api.location.domain

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.location.entity.CountryEntity
import com.tuturing.api.location.repository.CountryRepository
import com.tuturing.api.location.valueobject.JsonCallingCode
import java.io.BufferedReader
import java.util.*
import kotlin.collections.ArrayList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CountryService(
    @Autowired private val countryRepository: CountryRepository,
    @Value("classpath:json/calling_codes.json") private val callingCodesResource: Resource
) {
    lateinit var callingCodes: Map<String, Int>

    init {
        val content = callingCodesResource.inputStream.bufferedReader().use(BufferedReader::readText)
        val json = jacksonObjectMapper().readValue(content, object : TypeReference<List<JsonCallingCode>>() {})

        callingCodes = json.map {
            Pair(
                it.code,
                it.dialCode.replace("+", "").replace(" ", "").toInt()
            )
        }.toMap()
    }

    fun findByCountryCode(countryCode: String): CountryEntity? {
        return countryRepository.findByIsoCodeAlpha2(countryCode)
    }

    fun findById(id: UUID): CountryEntity? {
        return countryRepository.findByIdOrNull(id)
    }

    fun findAll(): List<CountryEntity> {
        return countryRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName")).toList()
    }

    fun save(country: CountryEntity) {
        countryRepository.save(country)
    }

    fun findByCountryCodeAndPopulateCountriesDatabase(countryCode: String): CountryEntity {
        val country = CountryEntity()
        val locale = Locale("", countryCode)

        country.isoCodeAlpha3 = locale.isO3Country.toString()
        country.isoCodeAlpha2 = locale.country.toString()
        country.fullName = locale.displayCountry.toString()
        country.phoneNumberCountryCode = callingCodes[countryCode]

        save(country)
        return country
    }

    fun populateCountriesDatabase(): List<CountryEntity> {
        // Map ISO countries to custom country object
        val countryCodes = Locale.getISOCountries()
        val countries: MutableList<CountryEntity> = ArrayList()

        for (countryCode in countryCodes) {
            countries.add(findByCountryCodeAndPopulateCountriesDatabase(countryCode))
        }
        return countries
    }
}
