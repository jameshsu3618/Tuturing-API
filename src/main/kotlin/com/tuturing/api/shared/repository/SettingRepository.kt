package com.tuturing.api.shared.repository

import com.tuturing.api.shared.entity.SettingEntity
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface SettingRepository : JpaRepository <SettingEntity, UUID> {
    fun findByKey(key: String): SettingEntity?
}
