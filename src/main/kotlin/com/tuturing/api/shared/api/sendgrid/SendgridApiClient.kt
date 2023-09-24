package com.tuturing.api.shared.api.sendgrid

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.shared.jms.EmailAddress
import com.tuturing.api.shared.jms.SendEmailResult
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

class SendgridApiClient(
    val host: String,
    val apiVersion: String,
    val apiKey: String,
    val client: OkHttpClient
) {
    private val logger = LoggerFactory.getLogger(SendgridApiClient::class.java)

    fun sendTemplatedEmail(
        subject: String,
        from: String,
        to: List<EmailAddress>,
        templateName: String,
        templateVariablesJson: String
    ): SendEmailResult {
        val mail = Mail()

        var dynamicTemplateVariables = jacksonObjectMapper().readValue(templateVariablesJson, object : TypeReference<Map<String?, Any?>?>() {})

        mail.setFrom(Email(from))
        mail.setReplyTo(Email(from))
        mail.setTemplateId(templateName)
        mail.setSubject(subject)

        val personalization = Personalization()
        for (recipient in to) {
            personalization.addTo(Email(recipient.email, recipient.name))
        }
        // map each email template variable to dynamic template data property of Personalization object
        dynamicTemplateVariables!!.forEach { (key, value) -> personalization.addDynamicTemplateData(key, value) }
        personalization.addDynamicTemplateData("subject", subject)
        personalization.subject = subject

        mail.addPersonalization(personalization)

        val mediaType = MediaType.parse("application/json,text/plain")
        val body = RequestBody.create(mediaType, mail.build())

        val request = Request.Builder()
            .url("$host/$apiVersion/mail/send")
            .addHeader("Content-Type", "application/json")
            .addHeader("Content-Type", "text/plain")
            .addHeader("Authorization", "Bearer $apiKey")
            .method("POST", body)
            .build()

        val result = kotlin.runCatching {
            client.newCall(request).execute()
        }

        return if (result.isSuccess) {
            val response = result.getOrNull()!!

            if (HttpStatus.ACCEPTED.value() != response.code()) {
                logger.error(
                    "Could not send email message via Sendgrid. Response status code: {} body {}",
                    response.code(), response.body()?.string()
                )
            }
            // https://sendgrid.com/docs/API_Reference/Web_API_v3/Mail/errors.html
            when (response.code()) {
                // Email is both valid, and queued to be delivered -> dequeue
                HttpStatus.ACCEPTED.value() -> SendEmailResult.SUCCESS
                // Email is valid, but it is not queued to be delivered.
                HttpStatus.OK.value() -> SendEmailResult.FAILED
                // Bad Request - Often missing a required parameter -> ignore and dequeue (most likely invalid template field)
                HttpStatus.BAD_REQUEST.value() -> SendEmailResult.REJECTED
                // Unauthorized - No valid API key provided -> try again
                HttpStatus.UNAUTHORIZED.value() -> SendEmailResult.FAILED
                // Not Found - The requested item doesn’t exist -> ignore and dequeue
                HttpStatus.NOT_FOUND.value() -> SendEmailResult.REJECTED
                // Request Entity Too Large - Attachment size is too big -> ignore and dequeue
                HttpStatus.PAYLOAD_TOO_LARGE.value() -> SendEmailResult.REJECTED
                // The number of requests you have made exceeds SendGrid’s rate limitations
                HttpStatus.TOO_MANY_REQUESTS.value() -> SendEmailResult.REJECTED
                // most likely 5xx, per Sendgrid documentation -> try again
                else -> SendEmailResult.FAILED
            }
        } else {
            logger.debug("Could not send email message via SendGrid, exception caught", result.exceptionOrNull()!!)
            SendEmailResult.FAILED
        }
    }
}
