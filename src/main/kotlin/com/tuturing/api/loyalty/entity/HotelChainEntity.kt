package com.tuturing.api.loyalty.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity()
@Table(name = "hotel_chain", indexes = [
        Index(name = "idx_loyalty_program_name", columnList = "loyaltyProgramName", unique = false)
])
@EntityListeners(AuditingEntityListener::class)
class HotelChainEntity(
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var fullName: String,

    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    var shortName: String?,

    @Column(nullable = false, columnDefinition = "CHAR(2)")
    var gdsCode: String,

    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    var loyaltyProgramName: String?
) : BaseEntity() {
        constructor() : this("", null, "", null)
}
