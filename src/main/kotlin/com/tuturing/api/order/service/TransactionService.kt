package com.tuturing.api.order.service

import com.tuturing.api.order.entity.TransactionEntity
import com.tuturing.api.order.repository.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionService(
    @Autowired val transactionRepository: TransactionRepository
) {
    fun save(transaction: TransactionEntity) {
        transactionRepository.save(transaction)
    }
}
