package com.tuturing.api.shared.jms

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micrometer.core.annotation.Timed
import javax.validation.constraints.NotEmpty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service

@Service
class SendEmailProducer(
    @Autowired val defaultJmsTemplate: JmsTemplate,
    @NotEmpty @Value("\${tuturing.emails.queue-name}") var queueName: String
) {
    private val logger = LoggerFactory.getLogger(SendEmailProducer::class.java)

    @Timed("aws.sqs", extraTags = [
        "queue", "\${tuturing.emails.queue-name}",
        "role", "producer"
    ])
    fun sendEmail(message: SendEmailMessage) {
        val json = jacksonObjectMapper().writeValueAsString(message)

        logger.debug("Producing message " + json)

        defaultJmsTemplate.convertAndSend(queueName, json)
    }
}
