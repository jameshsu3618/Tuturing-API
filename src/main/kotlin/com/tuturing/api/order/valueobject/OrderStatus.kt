package com.tuturing.api.order.valueobject

enum class OrderStatus {
    CREATED,
    PENDING,
    COMPLETE,
    PENDING_CANCELLATION,
    CANCELED,
    DECLINED
}
