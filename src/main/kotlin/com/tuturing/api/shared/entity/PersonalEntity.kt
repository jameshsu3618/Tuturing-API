package com.tuturing.api.shared.entity

import com.tuturing.api.user.entity.UserProfileEntity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.MappedSuperclass
import javax.persistence.OneToOne

@MappedSuperclass
class PersonalEntity() : BaseEntity() {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false, referencedColumnName = "id")
    lateinit var userProfile: UserProfileEntity
}
