package com.tuturing.api.user.entity

import com.tuturing.api.location.entity.CountryEntity
import com.tuturing.api.shared.entity.PersonalEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "user_profile_phone_number")
@EntityListeners(AuditingEntityListener::class, SabreTravelerProfileSyncListener::class)
class PhoneNumberEntity(
    @Column(nullable = false, columnDefinition = "VARCHAR(40)")
    var number: String
) : PersonalEntity() {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id")
    lateinit var country: CountryEntity

    constructor() : this("")

    @PrePersist
    fun onPrePersist() {
        updateNumber()
    }

    @PreUpdate
    fun onPreUpdate() {
        updateNumber()
    }

    fun updateNumber() {
        this.number = digitsOnlyRegex.replace(this.number, "")
    }

    private val digitsOnlyRegex = Regex("[^0-9]")
}
