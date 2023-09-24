package com.tuturing.api.shared.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "setting")
@EntityListeners(AuditingEntityListener::class)
class SettingEntity(
    @Column(name = "key", nullable = false, unique = true, columnDefinition = "VARCHAR(250)")
    var key: String,

    @Column(name = "value", nullable = false, unique = false, columnDefinition = "VARCHAR(250)")
    var value: String
) : BaseEntity() {
        constructor() : this("", "")
}
