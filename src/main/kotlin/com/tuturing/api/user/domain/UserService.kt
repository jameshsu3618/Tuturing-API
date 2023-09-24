package com.tuturing.api.user.domain

import com.tuturing.api.company.entity.CompanyEntity
import com.tuturing.api.company.entity.DepartmentEntity
import com.tuturing.api.policy.entity.PolicyEntity
import com.tuturing.api.security.CustomUserDetails
import com.tuturing.api.shared.service.AuthenticationFacade
import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.repository.UserProfileRepository
import com.tuturing.api.user.repository.UserRepository
import com.tuturing.api.user.valueobject.Role
import com.tuturing.api.user.valueobject.UserStatus
import java.util.UUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val userProfileRepository: UserProfileRepository,
    @Autowired private val authenticationFacade: AuthenticationFacade
) {
    fun refresh(user: UserEntity) {
        userRepository.refresh(user)
    }

    // don't annotate with authorization, too generic use case
    fun findById(id: UUID): UserEntity? {
        return userRepository.findByIdOrNull(id)
    }

    // don't annotate with authorization, too generic use case
    fun findAllByIds(ids: Iterable<UUID>): List<UserEntity> {
        return userRepository.findAllById(ids)
    }

    @PreAuthorize("#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()")
    @PostAuthorize("#tuturing.isDepartmental(returnObject)")
    fun findByIdAuthorized(id: UUID): UserEntity? {
        return userRepository.findOneWithInviterByIdOrNull(id)
    }

    // don't annotate with authorization, too generic use case
    fun findByEmail(email: String): UserEntity? {
        return userRepository.findByEmail(email)
    }

    @PreAuthorize("#tuturing.isAnAdmin()")
    @PostAuthorize("#tuturing.isCorporate(returnObject)")
    fun findByCompanyAndRoleAndStatus(company: CompanyEntity, role: Role, status: UserStatus): List<UserEntity> {
        return userRepository.findAllByCompanyIdAndRoleAndStatus(company.id!!, role, status)
    }

    @PreAuthorize("#tuturing.isAnAdmin() or #tuturing.isADepartmentManager() or #tuturing.isPersonal(#user)")
    @PostAuthorize("#tuturing.isCorporate(returnObject)")
    fun findTravelers(user: UserEntity): List<UserEntity> {
        return when (user.role) {
            Role.EMPLOYEE -> listOf(user)
            else -> userRepository.findAllByCompanyIdAndStatus(user.company.id!!, UserStatus.ACTIVATED)
        }
    }

    @PreAuthorize("#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()")
    @PostFilter("#tuturing.isDepartmental(filterObject)")
    fun findByCompany(company: CompanyEntity, offset: Int, count: Int): List<UserEntity> {
        val principal = authenticationFacade.authentication.principal as CustomUserDetails
        val sort = Sort.by("status").ascending()
            .and(Sort.by("profile.lastName").ascending())

        return userRepository.findAllByDepartment(
            principal.user.department,
            PageRequest.of(offset, count, sort)
        )
    }

    // don't annotate with authorization, too generic use case
    fun save(user: UserEntity): UserEntity {
        return userRepository.save(user)
    }

    @PreAuthorize("(#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()) " +
        "and #tuturing.isDepartmental(#user)")
    fun deactivate(user: UserEntity) {
        if (null == user.password || user.password!!.isEmpty()) {
            user.status = UserStatus.CANCELED
        } else {
            user.status = UserStatus.DEACTIVATED
        }
        this.save(user)
    }

    @PreAuthorize("(#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()) " +
        "and #tuturing.isDepartmental(#user)")
    fun activate(user: UserEntity) {
        if (user.password.isNullOrEmpty()) {
            user.status = UserStatus.CREATED
        } else {
            user.status = UserStatus.ACTIVATED
        }

        this.save(user)
    }

    @PreAuthorize("(#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()) " +
        "and #tuturing.isDepartmental(#user)")
    fun changeRole(user: UserEntity, role: Role): Boolean {
        if (role !in arrayOf(Role.EMPLOYEE, Role.COMPANY_ADMIN)) {
            return false
        }

        user.role = role
        this.save(user)

        return true
    }

    @PreAuthorize("(#tuturing.isAnAdmin() or #tuturing.isADepartmentManager()) " +
        "and #tuturing.isDepartmental(#user) and #tuturing.isDepartmental(#user.profile) and #tuturing.isDepartmental(#policy)")
    fun changeNameAndEmailAndPolicy(user: UserEntity, firstName: String?, lastName: String?, email: String, policy: PolicyEntity?) {
        firstName?.let { user.profile.firstName = it }
        lastName?.let { user.profile.lastName = it }
        userProfileRepository.save(user.profile)

        user.email = email
        user.policy = policy
        userRepository.save(user)
    }

    @PreAuthorize("(#tuturing.isAnAdmin() and #tuturing.isCorporate(#department)) or (#tuturing.isADepartmentManager() and #tuturing.isDepartmental(#department))")
    fun getActivatedEmployeeCountByDepartment(department: DepartmentEntity): Int {
        return userRepository.countByDepartmentAndStatus(department, UserStatus.ACTIVATED).toInt()
    }
}
