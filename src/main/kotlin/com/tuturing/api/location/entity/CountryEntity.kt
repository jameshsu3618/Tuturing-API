package com.tuturing.api.location.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "location_country")
@EntityListeners(AuditingEntityListener::class)
class CountryEntity(
    @Column(name = "full_name", nullable = false, columnDefinition = "VARCHAR(100)")
    var fullName: String,

    @Column(name = "iso_alpha_code_2", nullable = false, columnDefinition = "CHAR(2)")
    var isoCodeAlpha2: String,

    @Column(name = "iso_alpha_code_3", nullable = false, columnDefinition = "CHAR(3)")
    var isoCodeAlpha3: String,

        // TODO revert back to nullable = false and unique = true once there is a better source of countries
    @Column(name = "phone_number_country_code", nullable = true, columnDefinition = "INTEGER(3)")
    var phoneNumberCountryCode: Int?
) : BaseEntity() {
        @OneToMany(mappedBy = "country", fetch = FetchType.EAGER, cascade = arrayOf(CascadeType.ALL))
        @OrderBy("fullName")
        var subdivisions: MutableSet<SubdivisionEntity>? = HashSet()

        constructor(
            fullName: String,
            isoCodeAlpha2: String,
            isoCodeAlpha3: String,
            phoneNumberCountryCode: Int?,
            subdivisions: MutableSet<SubdivisionEntity>?
        ) : this(fullName, isoCodeAlpha2, isoCodeAlpha3, phoneNumberCountryCode) {
                this.subdivisions = subdivisions
        }

        constructor() : this("", "", "", null, mutableSetOf<SubdivisionEntity>())
}
