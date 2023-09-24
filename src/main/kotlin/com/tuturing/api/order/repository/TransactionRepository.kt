package com.tuturing.api.order.repository

import com.tuturing.api.order.entity.TransactionEntity
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<TransactionEntity, UUID>
