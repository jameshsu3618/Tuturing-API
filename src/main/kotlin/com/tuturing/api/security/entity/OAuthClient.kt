package com.tuturing.api.security.entity

import com.tuturing.api.shared.entity.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "oauth_client")
@EntityListeners(AuditingEntityListener::class)
data class OAuthClient(
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(50)")
    var name: String,

    @Column(name = "secret", nullable = true, columnDefinition = "VARCHAR(100)")
    var secret: String
) : BaseEntity() {
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
            name = "oauth_client_granted_scopes",
            joinColumns = [JoinColumn(name = "oauth_client_id")],
            inverseJoinColumns = [JoinColumn(name = "oauth_scope_id")]
        )
        lateinit var scopes: MutableSet<OAuthScope>
}
