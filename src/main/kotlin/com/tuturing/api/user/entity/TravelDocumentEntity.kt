package com.tuturing.api.user.entity

import com.tuturing.api.location.entity.CountryEntity
import com.tuturing.api.shared.entity.PersonalEntity
import java.time.LocalDate
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

private const val NUMBER_MASK_LENGTH = 4

@Entity(name = "user_profile_travel_document")
@EntityListeners(AuditingEntityListener::class, SabreTravelerProfileSyncListener::class)
class TravelDocumentEntity(
        // https://en.wikipedia.org/wiki/Machine-readable_passport
        // passport number is 9 characters + 1 check digit
        // the national identification number can be longer, e.g. China uses 17 characters long IDs
    @Column(name = "number", nullable = false, columnDefinition = "VARCHAR(25)")
    var number: String,

    @Column(name = "issue_date", nullable = false)
    var issueDate: LocalDate,

    @Column(name = "expiration_date", nullable = false)
    var expirationDate: LocalDate

) : PersonalEntity() {
        @OneToOne()
        @JoinColumn(name = "nationality_country_id", nullable = false, referencedColumnName = "id")
        lateinit var nationality: CountryEntity

        @OneToOne()
        @JoinColumn(name = "issuing_country_id", nullable = false, referencedColumnName = "id")
        lateinit var issuingCountry: CountryEntity

        constructor() : this(
                "",
                LocalDate.now(), LocalDate.now()
        ) {}

        fun maskNumber(): String {
                return if (number.length >= NUMBER_MASK_LENGTH) {
                        number.substring(number.length - NUMBER_MASK_LENGTH)
                } else {
                        number
                }
        }
}
