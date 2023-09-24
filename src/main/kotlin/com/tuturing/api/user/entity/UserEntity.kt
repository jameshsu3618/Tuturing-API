package com.tuturing.api.user.entity

import com.tuturing.api.policy.entity.PolicyEntity
import com.tuturing.api.shared.entity.DepartmentalEntity
import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserStatus
import java.time.LocalDateTime
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.PrePersist
import javax.persistence.PreUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity(name = "user")
@EntityListeners(AuditingEntityListener::class, SabreTravelerProfileSyncListener::class)
class UserEntity(
    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(250)")
    var email: String,

    @Column(name = "password", nullable = true, columnDefinition = "VARCHAR(60)")
    var password: String?,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role
) : DepartmentalEntity() {
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = arrayOf(CascadeType.ALL), optional = false, orphanRemoval = true)
    lateinit var profile: UserProfileEntity

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", referencedColumnName = "id", nullable = true, unique = false)
    var inviter: UserEntity? = null

    @Column(name = "invited_at", nullable = true)
    var invitedAt: LocalDateTime? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "id", nullable = true, unique = false)
    var policy: PolicyEntity? = null

    constructor() : this("", "", UserStatus.ACTIVATED, Role.EMPLOYEE) {}

    @PrePersist
    fun onPrePersist() {
        updateEmail()
    }

    @PreUpdate
    fun onPreUpdate() {
        updateEmail()
    }

    fun updateEmail() {
        this.email = this.email.toLowerCase()
    }
}
