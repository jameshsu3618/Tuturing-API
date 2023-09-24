package com.tuturing.api.location.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "location_subdivision")
@EntityListeners(AuditingEntityListener::class)
class SubdivisionEntity(
    @Column(name = "full_name", nullable = true, columnDefinition = "VARCHAR(100)")
    var fullName: String,

        // some territories contain 6 char like AU-NSW for New South Wales
    @Column(name = "iso_subdivision_code", nullable = false, columnDefinition = "CHAR(6)")
    var isoSubdivisionCode: String,

    @Column(name = "country_code", nullable = false, columnDefinition = "CHAR(5)")
    var countryCode: String

) : BaseEntity() {
    @OneToOne(fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id")
    lateinit var country: CountryEntity

    constructor(fullName: String, isoSubdivisionCode: String, countryCode: String, country: CountryEntity) : this(fullName, isoSubdivisionCode, countryCode) { this.country = country }

    constructor() : this("", "", "", CountryEntity())

    fun subdivisionCodeWithoutCountryCode(): String {
        return isoSubdivisionCode.replace(countryCode + "-", "")
    }

    val subdivisionWithoutCountry: String
        get() = this.subdivisionCodeWithoutCountryCode()
}
