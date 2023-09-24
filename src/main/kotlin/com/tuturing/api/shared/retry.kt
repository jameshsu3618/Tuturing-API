package com.tuturing.api.shared

import kotlinx.coroutines.delay

/**
 * Retry logic for co-routines.
 *
 * Based on: https://stackoverflow.com/a/46890009/35341
 */
suspend fun <T> retry(
    times: Int,
    initialDelay: Long = 100, // 0.1 second
    maxDelay: Long = 1000, // 1 second
    factor: Double = 2.0,
    catchCallback: ((index: Int, delay: Long) -> Unit)? = null,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            if (catchCallback != null) {
                catchCallback(it, currentDelay)
            }
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block() // last attempt
}
