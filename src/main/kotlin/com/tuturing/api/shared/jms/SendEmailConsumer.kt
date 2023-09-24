package com.tuturing.api.shared.jms

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tuturing.api.shared.api.sendgrid.SendgridApiClient
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Service

@Service
class SendEmailConsumer(
    @Autowired val sendgrid: SendgridApiClient,
    @Autowired val meterRegistry: MeterRegistry
) {
    private val logger = LoggerFactory.getLogger(SendEmailConsumer::class.java)

    private val successCounter = meterRegistry.counter("emails_sent", listOf(Tag.of("result", "SUCCESS")))
    private val rejectedCounter = meterRegistry.counter("emails_sent", listOf(Tag.of("result", "REJECTED")))
    private val failureCounter = meterRegistry.counter("emails_sent", listOf(Tag.of("result", "FAILURE")))

    @Timed("aws.sqs", extraTags = [
        "queue", "\${tuturing.emails.queue-name}",
        "role", "consumer"
    ])
    @JmsListener(destination = "\${tuturing.emails.queue-name}")
    fun receiveQueueMessage(plainMessage: String) {
        logger.debug("Consuming message " + plainMessage)

        val message: SendEmailMessage? = try {
            jacksonObjectMapper().readValue(plainMessage, SendEmailMessage::class.java)
        } catch (e: JsonParseException) {
            logger.debug("Could not parse email message JSON")
            null
        } catch (e: JsonMappingException) {
            logger.debug("Could not map email message to SendEmailMessage")
            null
        }

        message?.let {
            val result = sendgrid.sendTemplatedEmail(
                it.subject, it.sender, it.recipients, it.templateName, it.templateVariablesJson
            )

            when (result) {
                SendEmailResult.SUCCESS -> {
                    logger.debug("Email message sent")
                    successCounter.increment()
                }
                SendEmailResult.REJECTED -> {
                    logger.warn("Email message has been rejected, {}", plainMessage)
                    rejectedCounter.increment()
                }
                SendEmailResult.FAILED -> {
                    failureCounter.increment()
                    throw SendEmailException("Could not send email, will retry later")
                }
            }
        }
    }
}
