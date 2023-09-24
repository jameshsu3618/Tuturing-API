package com.tuturing.api.loyalty.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "airline_alliance")
@EntityListeners(AuditingEntityListener::class)
class AirlineAllianceEntity(
    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    var name: String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
            name = "airline_alliance_airlines",
            joinColumns = [JoinColumn(name = "airline_alliance_id")],
            inverseJoinColumns = [JoinColumn(name = "airline_id")]
    )
    var airlines: MutableSet<AirlineEntity>
) : BaseEntity() {
        constructor() : this("", HashSet())
}
