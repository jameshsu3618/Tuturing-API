package com.tuturing.api.shared.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

interface Auditable {
    var createdAt: LocalDateTime?
    var updatedAt: LocalDateTime?
}

@Embeddable
data class Audited(
    @Column(columnDefinition = "DATETIME", name = "created_at", nullable = false)
    @CreatedDate
    override var createdAt: LocalDateTime? = null,

    @Column(columnDefinition = "DATETIME", name = "updated_at", nullable = false)
    @LastModifiedDate
    override var updatedAt: LocalDateTime? = null
) : Auditable
