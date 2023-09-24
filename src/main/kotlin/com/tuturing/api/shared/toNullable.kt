package com.tuturing.api.shared

import java.util.*

fun <T : Any> Optional<T>.toNullable(): T? = this.orElse(null)
