package com.tuturing.api.user.entity

import com.tuturing.api.loyalty.entity.AirlineEntity
import com.tuturing.api.shared.entity.PersonalEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "user_profile_airline_loyalty_program")
@EntityListeners(AuditingEntityListener::class, SabreTravelerProfileSyncListener::class)
class AirlineLoyaltyProgramEntity(
    @Column(name = "number", nullable = false, columnDefinition = "VARCHAR(25)")
    var number: String
) : PersonalEntity() {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airline_id", referencedColumnName = "id", nullable = false, unique = false)
    lateinit var airline: AirlineEntity

    constructor() : this("")
}
