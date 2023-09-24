package com.tuturing.api.security

import com.tuturing.api.company.valueobject.CompanyStatus
import com.tuturing.api.user.domain.UserService
import com.tuturing.api.user.valueobject.UserStatus
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService : UserDetailsService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var userService: UserService

    @Transactional // we are lazy loading company, must annotate as Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug("Finding user by email {}", username)

        val user = userService.findByEmail(username)

        return user?.let {
            logger.debug("User {} found", it.email)
//            logger.debug("User {} status {}", it.email, it.status)
//            logger.debug("User {} company {}", it.email, it.company.id)
//            logger.debug("User {} company status {}", it.email, it.company.status)

            if (UserStatus.ACTIVATED != it.status || CompanyStatus.ACTIVATED != it.company.status) {
                logger.debug("Not activated, user status {} company status {}", it.status, it.company.status)

                throw UsernameNotFoundException("User or company is not activated")
            }

            logger.debug("User {} is authorized, creating CustomUserDetails", it.email)

            // alternatively, we could use a org.springframework.core.convert.converter.Converter
            CustomUserDetails(it)
        } ?: throw UsernameNotFoundException("User not found, username/email " + username)
    }
}
