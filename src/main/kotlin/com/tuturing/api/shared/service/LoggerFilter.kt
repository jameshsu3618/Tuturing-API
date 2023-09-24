package com.tuturing.api.shared.service

import com.tuturing.api.security.CustomUserDetails
import java.util.*
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component

@Component
class LoggerFilter(
    @Autowired private val authenticationFacade: AuthenticationFacade
) : Filter {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        try {
            MDC.clear()
            MDC.put("requestId", UUID.randomUUID().toString())

            getRemoteAddr(request)?.let {
                MDC.put("remoteAddress", it)
            }

            if (!authenticationFacade.hasAuthentication) {
                MDC.put("isAuthenticated", false.toString())
            } else {
                MDC.put("isAuthenticated", authenticationFacade.authentication.isAuthenticated.toString())

//                if (authenticationFacade.authentication.isAuthenticated) {
//                    val principal = authenticationFacade.authentication.principal
//                    when (principal) {
//                        is CustomUserDetails -> MDC.put("userId", principal.user.id.toString())
//                        is String -> MDC.put("clientId", principal)
//                        is User -> MDC.put("username", principal.username)
//                        else -> logger.warn("Unknown type of principal: {}", principal::class.java)
//                    }
//                }
            }

            chain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }

    override fun init(filterConfig: FilterConfig) {
    }

    override fun destroy() {}

    private fun getRemoteAddr(request: ServletRequest): String? {
        if (request is HttpServletRequest) {
            val ipFromHeader = request.getHeader("X-FORWARDED-FOR")
            if (ipFromHeader != null && ipFromHeader.length > 0) {
                return ipFromHeader
            }
        }

        return request.remoteAddr
    }
}
