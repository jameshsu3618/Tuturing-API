package com.tuturing.api.security.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.ManyToMany
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "oauth_scope")
@EntityListeners(AuditingEntityListener::class)
data class OAuthScope(
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(100)")
    var name: String
) : BaseEntity() {
        @ManyToMany(mappedBy = "scopes")
        lateinit var clients: MutableSet<OAuthClient>
}
