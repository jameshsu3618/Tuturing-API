package com.tuturing.api.user.entity

import com.tuturing.api.loyalty.entity.HotelChainEntity
import com.tuturing.api.shared.entity.PersonalEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "user_profile_hotel_chain_loyalty_program")
@EntityListeners(AuditingEntityListener::class)
class HotelChainLoyaltyProgramEntity(
    @Column(name = "number", nullable = false, columnDefinition = "VARCHAR(25)")
    var number: String
) : PersonalEntity() {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_chain_id", referencedColumnName = "id", nullable = false, unique = false)
    lateinit var hotelChain: HotelChainEntity

    constructor() : this("")
}
