package com.tuturing.api.order.service.exception

sealed class CancellationException(val reason: String, message: String) : Throwable(message) {
    object RefundMismatch : CancellationException("REFUND_MISMATCH", "Refund amount has changed")
    class UnprocessableEntity(message: String) : CancellationException("UNPROCESSABLE_ENTITY", message)
    class NotFound(message: String) : CancellationException("NOT_FOUND", message)
    class APIError(message: String) : CancellationException("API_ERROR", message)
}
