package com.tuturing.api.user.domain

import com.tuturing.api.user.entity.UserEntity
import com.tuturing.api.user.repository.UserRepository
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

private const val MIN_SEARCH_STRING_LENGTH = 3

@Service
class UserSearchService(
    @Autowired val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PreAuthorize("#oauth2.hasScope('superadmin.user:read')")
    fun findAll(id: UUID?, email: String?, firstName: String?, lastName: String?): List<UserEntity> {
        logger.debug("Finding all users by id = {} email = {} first name = {} last name = {}",
            id, email, firstName, lastName
        )

        return if (null == id && null == email && null == firstName) {
            logger.debug("Can not find users, id, email and first name are null")
            listOf()
        } else if (null != email && email.length < MIN_SEARCH_STRING_LENGTH) {
            logger.debug("Can not find users, email is too short")
            listOf()
        } else if (null != firstName && firstName.length < MIN_SEARCH_STRING_LENGTH) {
            logger.debug("Can not find users, first name is too short")
            listOf()
        } else if (null != lastName && lastName.length < MIN_SEARCH_STRING_LENGTH) {
            logger.debug("Can not find users, last name is too short")
            listOf()
        } else {
            logger.debug("Executing the database query to find users")
            userRepository.findAlByIdOrEmailOrFirstNameOrLastName(id, email, firstName, lastName)
        }
    }
}
