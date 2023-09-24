package com.tuturing.api.user.entity

import com.tuturing.api.paymentmethod.entity.PersonalCardEntity
import com.tuturing.api.shared.entity.DepartmentalEntity
import com.tuturing.api.user.valueobject.Gender
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.Index
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table
import org.springframework.data.jpa.domain.support.AuditingEntityListener

private const val KNOWN_TRAVELER_NUMBER_MASK_LENGTH = 4
private const val REDRESS_NUMBER_MASK_LENGTH = 4

@Entity(name = "user_profile")
@EntityListeners(AuditingEntityListener::class, SabreTravelerProfileSyncListener::class)
@Table(name = "user_profile",
    indexes = [Index(columnList = "first_name"), Index(columnList = "last_name")]
)
class UserProfileEntity(
    @Column(name = "first_name", nullable = false, columnDefinition = "VARCHAR(200)")
    var firstName: String,

    @Column(name = "middle_name", nullable = true, columnDefinition = "VARCHAR(200)")
    var middleName: String?,

    @Column(name = "last_name", nullable = false, columnDefinition = "VARCHAR(200)")
    var lastName: String,

    @Column(name = "prefix", nullable = true, columnDefinition = "VARCHAR(10)")
    var prefix: String?,

    @Column(name = "suffix", nullable = true, columnDefinition = "VARCHAR(10)")
    var suffix: String?,

    @Column(name = "birth_date", nullable = true)
    var birthDate: LocalDate?,

    @Column(name = "known_traveler_number", nullable = true, columnDefinition = "VARCHAR(20)")
    var knownTravelerNumber: String?,

    @Column(name = "redress_number", nullable = true, columnDefinition = "VARCHAR(10)")
    var redressNumber: String?,

    @Column(name = "gender", nullable = true)
    @Enumerated(EnumType.STRING)
    var gender: Gender?,

    @OneToOne(fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    var user: UserEntity
) : DepartmentalEntity() {
    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    var travelDocuments: MutableSet<TravelDocumentEntity> = HashSet()

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    var phoneNumbers: MutableSet<PhoneNumberEntity> = HashSet()

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    var personalCards: MutableSet<PersonalCardEntity> = HashSet()

    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.ALL))
    var airlineLoyaltyPrograms: MutableSet<AirlineLoyaltyProgramEntity> = HashSet()

    constructor() : this(
        "", "", "",
        null, null, null,
        null, null, null, UserEntity()
    )

    fun fullName(): String {
        return firstName + " " + lastName
    }

    val fullName: String
        get() = this.fullName()

    fun maskedKnownTravelerNumber(): String? {
        return knownTravelerNumber?.let {
            if (it.length >= KNOWN_TRAVELER_NUMBER_MASK_LENGTH) {
                it.substring(it.length - KNOWN_TRAVELER_NUMBER_MASK_LENGTH)
            } else {
                it
            }
        } ?: return null
    }

    fun maskedRedressNumber(): String? {
        return redressNumber?.let {
            if (it.length >= REDRESS_NUMBER_MASK_LENGTH) {
                it.substring(it.length - REDRESS_NUMBER_MASK_LENGTH)
            } else {
                it
            }
        } ?: return null
    }
}
