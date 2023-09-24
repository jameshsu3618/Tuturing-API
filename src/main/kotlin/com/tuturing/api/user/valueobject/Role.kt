package com.tuturing.api.user.valueobject

enum class Role {
    SUPER_ADMIN,
    ACCOUNT_OWNER,
    COMPANY_ADMIN,
    DEPARTMENT_MANAGER,
    EMPLOYEE;

    companion object {
        val adminRoles = listOf(SUPER_ADMIN, ACCOUNT_OWNER, COMPANY_ADMIN)
    }
}
