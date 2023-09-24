package com.tuturing.api.shared.valueobject

import java.math.BigDecimal

data class Distance(
    val value: BigDecimal,
    val unit: DistanceUnit
)
