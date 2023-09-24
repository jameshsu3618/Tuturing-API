package com.tuturing.api.shared.entity

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null,

    @Embedded
    val auditable: Audited = Audited()
) : Auditable by auditable {
    override fun toString(): String {
        return "${this::class.simpleName}(id = \"${id}\")"
    }
}
