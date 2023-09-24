package com.tuturing.api.shared.controller

import java.io.IOException
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class ControllerAdvice : ResponseEntityExceptionHandler() {
    private val theLogger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ConstraintViolationException::class)
    @Throws(IOException::class)
    fun constraintViolationException(response: HttpServletResponse) {
        theLogger.warn(
            "Caught ConstraintViolationException, responding with {} {}",
            HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.name
        )
        response.sendError(HttpStatus.BAD_REQUEST.value())
    }
}
