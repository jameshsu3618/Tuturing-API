package com.tuturing.api.shared.api.recaptcha

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.shared.api.recaptcha.dto.RecaptchaResponseDto
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory

class RecaptchaApiClient(val secretKey: String, val scoreThreshold: Double, val okhttpClient: OkHttpClient, val host: String) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun validateResponse(clientIP: String?, token: String, action: String): Boolean {

        val urlBuilder = HttpUrl.parse(host)!!
                .newBuilder()
                .addQueryParameter("secret", secretKey)
                .addQueryParameter("response", token)
                .addQueryParameter("action", action)

        val url = if (clientIP == null) {
            urlBuilder.build().toString()
        } else {
            urlBuilder.addQueryParameter("remoteip", clientIP).build().toString()
        }

        val request = Request.Builder().url(url).build()

        val result = kotlin.runCatching {
            okhttpClient.newCall(request).execute()
        }
        return if (result.isSuccess) {
            val response = result.getOrNull()!!
            val body = response.body()!!.string()
            val responseDto = jacksonObjectMapper().readValue(body, RecaptchaResponseDto::class.java)
            if (responseDto.success) {
                responseDto.score >= scoreThreshold
            } else {
                logger.error("Recaptcha validation failed: {}", body)
                false
            }
        } else {
            logger.error("Recaptcha validation failed")
            false
        }
    }
}
