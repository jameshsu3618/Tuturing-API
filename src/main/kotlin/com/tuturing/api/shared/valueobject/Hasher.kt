package com.tuturing.api.shared.valueobject

import java.security.MessageDigest
import java.util.*

object Hasher {
    fun hashString(input: String, algorithm: String): String {
        return MessageDigest
                .getInstance(algorithm)
                .digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
    }

    fun getTimeStamp(): Long {
        val date = Date()
        return date.time / 1000
    }
}
