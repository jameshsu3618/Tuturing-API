package com.tuturing.api.shared.repository

import java.io.Serializable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

/**
 * Base repository for all tuturing JPA repositories.
 *
 * Expands the functionality of repositories by adding `refresh`
 * method, to refresh/reload stale entities - a use case
 * we commonly encounter when creating complex structures of
 * entities dependencies.
 */
@NoRepositoryBean
interface CustomRepository<T, ID : Serializable?> : JpaRepository<T, ID> {
    /**
     * Refreshes the entity state by reloading data from the database.
     * This is useful in scenarios where between loading/creating an
     * entity and performing a business logic operation the database
     * could have changed. For example, a workflow needs to create
     * entities A and B. A should reference B, however, B was created
     * after creating A and A doesn't know about those changes yet.
     * The easiest and safest way to reload data, including
     * the dependenices, is to refresh the entity.
     *
     * Note, refresh will overwrite any changes made.
     *
     * More information:
     * https://en.wikibooks.org/wiki/Java_Persistence/Persisting#Refresh
     * https://docs.oracle.com/javaee/7/api/javax/persistence/EntityManager.html#refresh-java.lang.Object-
     */
    fun refresh(t: T)
}
