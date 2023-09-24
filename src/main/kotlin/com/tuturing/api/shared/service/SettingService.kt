package com.tuturing.api.shared.service

import com.tuturing.api.shared.entity.SettingEntity
import com.tuturing.api.shared.repository.SettingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SettingService(
    @Autowired val settingRepository: SettingRepository
) {
    fun findByKeyOrDefault(key: String, defaultValue: String): SettingEntity {
        var setting = settingRepository.findByKey(key)

        if (null == setting) {
            setting = SettingEntity(key, defaultValue)
            settingRepository.save(setting)
        }

        return setting
    }

    fun save(setting: SettingEntity) {
        settingRepository.save(setting)
    }
}
