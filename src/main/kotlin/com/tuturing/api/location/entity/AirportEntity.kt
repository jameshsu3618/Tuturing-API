package com.tuturing.api.location.entity

import com.tuturing.api.shared.entity.BaseEntity
import java.math.BigDecimal
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity()
@Table(name = "airport", indexes = [
    Index(name = "idx_iata_code", columnList = "iataCode", unique = true)
])
@EntityListeners(AuditingEntityListener::class)
class AirportEntity(
    @Column(nullable = true, columnDefinition = "VARCHAR(100)")
    var fullName: String?,

    @Column(nullable = false, columnDefinition = "VARCHAR(3)")
    var iataCode: String,

    @Column(nullable = true, columnDefinition = "VARCHAR(200)")
    var city: String?,

    @Column(nullable = true, columnDefinition = "VARCHAR(100)")
    var region: String?,

    @Column(nullable = true, columnDefinition = "VARCHAR(100)")
    var country: String?,

    @Column(nullable = true, columnDefinition = "DECIMAL(10,6)")
    var latitude: BigDecimal?,

    @Column(nullable = true, columnDefinition = "DECIMAL(10,6)")
    var longitude: BigDecimal?,

    @Column(nullable = true, columnDefinition = "VARCHAR(64)")
    var timezone: String?

) : BaseEntity() {
    constructor() : this(null, "", null, null, null, null, null, null)
}
