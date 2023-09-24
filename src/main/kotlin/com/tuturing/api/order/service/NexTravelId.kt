package com.tuturing.api.order.service

import java.security.SecureRandom
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class tuturingId(
    @Autowired private val redisTemplate: StringRedisTemplate
) {
    companion object {
        private const val ID_LENGTH = 16
        private const val LOCK_KEY_PREFIX = "tuturingid:"
        private const val LOCK_VALUE = "1" // anything
        private const val LOCK_LENGTH = 120L // 2 minutes
    }

    // do not change
    // any recent date would do, but 2020-02-02 is fun
    private val startDate = LocalDateTime
            .of(2020, 2, 2, 20, 20, 20)
            .toEpochSecond(ZoneOffset.UTC)

    private val secureRandom = SecureRandom.getInstance("NativePRNGNonBlocking")

    fun random(): String {
        var randomId: String

        do {
            randomId = nextRandom()
            val key = LOCK_KEY_PREFIX + randomId
            val hasSetLock = redisTemplate
                    .opsForValue()
                    .setIfAbsent(key, LOCK_VALUE, Duration.ofSeconds(LOCK_LENGTH))
                    ?: false
        } while (!hasSetLock)

        return randomId
    }

    private fun nextRandom(): String {
        val diffInSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - startDate
        val diffInMinutes = diffInSeconds / 60
        var result = diffInMinutes.toString()

        while (result.length < ID_LENGTH) {
            result += abs(secureRandom.nextLong()).toString()
        }

        return result.take(ID_LENGTH)
    }
}
