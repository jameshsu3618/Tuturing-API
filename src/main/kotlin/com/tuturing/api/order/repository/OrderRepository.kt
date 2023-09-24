package com.tuturing.api.order.repository

import com.tuturing.api.order.entity.OrderEntity
import com.tuturing.api.shared.repository.CustomRepository
import java.util.UUID

interface OrderRepository : CustomRepository<OrderEntity, UUID> {
    fun findByPublicId(publicId: String): OrderEntity?
}
