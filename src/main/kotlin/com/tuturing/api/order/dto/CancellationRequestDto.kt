package com.tuturing.api.order.dto

data class CancellationRequestDto(
    val expectedRefundAmount: String,
    val expectedRefundCurrency: String
)
