package com.tuturing.api.location.controller

import com.tuturing.api.location.domain.CountryService
import com.tuturing.api.location.domain.SubdivisionService
import com.tuturing.api.location.dto.CountryDto
import com.tuturing.api.location.dto.SubdivisionDto
import com.tuturing.api.location.mapper.CountryMapper
import com.tuturing.api.location.mapper.SubdivisionMapper
import com.tuturing.api.shared.service.AuthenticationFacade
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/location/countries")
class CountryController(
    @Autowired private val authenticationFacade: AuthenticationFacade,
    @Autowired private val countryService: CountryService,
    @Autowired private val subdivisionService: SubdivisionService,
    @Autowired private val countryMapper: CountryMapper,
    @Autowired private val subdivisionMapper: SubdivisionMapper

) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.isUser()")
    @GetMapping("")
    fun getCountries(): ResponseEntity<List<CountryDto>> {
        val countries = countryService.findAll()

        val dto = countries.map { country -> countryMapper.convertToDto(country) }

        return ResponseEntity.ok(dto)
    }

    @PreAuthorize("#oauth2.isUser() and hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("")
    fun listCountries(): ResponseEntity<Collection<CountryDto>> {
        return try {
            countryService.populateCountriesDatabase()
            ResponseEntity.ok().build<Collection<CountryDto>>()
        } catch (iae: IllegalArgumentException) {
            // could not map, most likely due to invalid request, e.g. incorrect formatting of card details
            ResponseEntity.badRequest().build()
        }
    }

    @PreAuthorize("#oauth2.isUser() and hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("{countryCode}/subdivisions")
    fun listOfSubdivisions(@PathVariable countryCode: String): ResponseEntity<List<SubdivisionDto>> {
        return try {
            val country = countryService.findByCountryCode(countryCode) ?: countryService.findByCountryCodeAndPopulateCountriesDatabase(countryCode)
            val subdivisions = subdivisionService.findOrRetrieveListOfSubdivisions(country)
            val dto = subdivisions!!.map { sub -> subdivisionMapper.convertToDto(sub) }
            ResponseEntity.ok(dto)
        } catch (iae: IllegalArgumentException) {
            // could not map, most likely due to invalid request, e.g. incorrect formatting of card details
            ResponseEntity.badRequest().build()
        }
    }
}
