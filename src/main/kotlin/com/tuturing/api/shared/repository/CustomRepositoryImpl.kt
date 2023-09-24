package com.tuturing.api.shared.repository

import java.io.Serializable
import javax.persistence.EntityManager
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.transaction.annotation.Transactional

class CustomRepositoryImpl<T, ID : Serializable?>(
    entityInformation: JpaEntityInformation<T, *>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, ID>(entityInformation, entityManager), CustomRepository<T, ID> {
    @Transactional
    override fun refresh(t: T) {
        entityManager.refresh(t)
    }
}
