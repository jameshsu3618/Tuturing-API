package com.tuturing.api.shared.dto.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ErrorResponse private constructor(
    val error: Error
) {
    data class Builder(
        private var reason: String? = null,
        private var message: String? = null,
        private var invalidFields: MutableList<FieldError>? = null
    ) {

        fun reason(reason: String) = apply {
            this.reason = reason
        }

        fun reason(reason: String, message: String) = apply {
            this.reason = reason
            this.message = message
        }

        fun invalidFields(fields: List<FieldError>) = apply {
            this.invalidFields = fields.toMutableList()
        }

        fun invalidField(field: String, reason: String, message: String) = apply {
            if (null == this.invalidFields) {
                this.invalidFields = mutableListOf<FieldError>()
            }

            this.invalidFields?.add(FieldError(reason, message, field))
        }

        fun build() = ErrorResponse(Error(reason, message, invalidFields))

        fun buildHttpResponse(status: HttpStatus): ResponseEntity<Any> {
            return ResponseEntity
                    .status(status)
                    .body(build())
        }
    }

    companion object {
        fun from(reason: String): Builder {
            return Builder().reason(reason)
        }

        fun from(reason: String, message: String): Builder {
            return Builder().reason(reason, message)
        }

        fun from(t: Throwable): Builder {
            return Builder().reason(
                    t::class.simpleName ?: "UNKNOWN_ERROR",
                    t.message ?: "No message"
            )
        }

        fun internalServerError(): ResponseEntity<Any> {
            return from(HttpStatus.INTERNAL_SERVER_ERROR.name).buildHttpResponse(HttpStatus.INTERNAL_SERVER_ERROR)
        }

        fun unprocessableEntity(reason: String, message: String?): ResponseEntity<Any> {
            return from(reason, message ?: "No message").buildHttpResponse(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }
}
