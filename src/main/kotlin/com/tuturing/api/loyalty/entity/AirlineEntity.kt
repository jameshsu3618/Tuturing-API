package com.tuturing.api.loyalty.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity()
@Table(name = "airline", indexes = [
        Index(name = "idx_loyalty_program_name", columnList = "loyaltyProgramName", unique = false)
])
@EntityListeners(AuditingEntityListener::class)
class AirlineEntity(
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var fullName: String,

    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    var shortName: String?,

    @Column(nullable = false, columnDefinition = "CHAR(2)")
    var iataCode: String,

    @Column(nullable = true, columnDefinition = "VARCHAR(3)")
    var iataAccountingCodePax: String?,

    @Column(nullable = true, columnDefinition = "VARCHAR(3)")
    var iataAirlinePrefixCode: String?,

    @Column(nullable = false, columnDefinition = "VARCHAR(4)")
    var icaoCode: String,

    @Column(nullable = true, columnDefinition = "VARCHAR(50)")
    var loyaltyProgramName: String?,

    @ManyToMany(mappedBy = "airlines", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var alliances: MutableSet<AirlineAllianceEntity>
) : BaseEntity() {
        constructor() : this("", null, "", null, null, "", null, HashSet())
}
