package com.tuturing.api.user.repository

import com.tuturing.api.company.entity.DepartmentEntity
import com.tuturing.api.shared.repository.CustomRepository
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserStatus
import java.util.UUID
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : CustomRepository<UserEntity, UUID> {
    @Query("select u from user u left join fetch u.inviter i where u.id = :id")
    fun findOneWithInviterByIdOrNull(@Param("id") id: UUID): UserEntity?

    fun findByEmail(email: String): UserEntity?

    @Query("SELECT u FROM user u " +
        "INNER JOIN u.department " +
        "WHERE u.company = :#{#department.company} " +
        "AND u.department.leftLimit >= :#{#department.leftLimit} " +
        "AND u.department.rightLimit <= :#{#department.rightLimit}")
    fun findAllByDepartment(
        @Param("department") department: DepartmentEntity,
        pageable: Pageable
    ): List<UserEntity>

    fun findAllByCompanyIdAndStatus(id: UUID, status: UserStatus): List<UserEntity>

    fun findAllByCompanyIdAndRoleAndStatus(id: UUID, role: Role, status: UserStatus): List<UserEntity>

    @Query("select u from user u " +
        "left join fetch u.profile up " +
        "left join fetch up.phoneNumbers uppn " +
        "left join u.company c " +
        "where " +
        "(:id is null or u.id = :id) and " +
        "(:email is null or u.email = :email) and " +
        "(:firstName is null or up.firstName = :firstName) and" +
        "(:lastName is null or up.lastName = :lastName)")
    fun findAlByIdOrEmailOrFirstNameOrLastName(
        @Param("id") id: UUID?,
        @Param("email") email: String?,
        @Param("firstName") firstName: String?,
        @Param("lastName") lastName: String?
    ): List<UserEntity>

    fun countByDepartmentAndStatus(department: DepartmentEntity, status: UserStatus): Long
}
