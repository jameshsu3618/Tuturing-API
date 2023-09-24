package com.tuturing.api.shared.controller.filter

import java.io.IOException
import javax.servlet.*
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

class MaintenanceFilter(val isEnabled: Boolean) : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (isEnabled) {
            (response as HttpServletResponse).status = HttpStatus.SERVICE_UNAVAILABLE.value()
        } else {
            chain.doFilter(request, response)
        }
    }
}
