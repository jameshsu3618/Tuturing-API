package com.tuturing.api.configuration

import com.amazon.sqs.javamessaging.SQSConnectionFactory
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import javax.jms.Session
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.destination.DynamicDestinationResolver

@Configuration
@EnableJms
class JMSConfiguration(
    @NotEmpty @Value("\${tuturing.aws.sqs.region}") var region: String,
    @NotEmpty @Value("\${tuturing.aws.sqs.access-key}") var accessKey: String,
    @NotEmpty @Value("\${tuturing.aws.sqs.secret-key}") var secretKey: String,
    @NotEmpty @Value("\${tuturing.aws.sqs.endpoint}") var endpoint: String
) {
    val connectionFactory: SQSConnectionFactory = SQSConnectionFactory.builder()
            .withRegion(Region.getRegion(Regions.fromName(region)))
            .withAWSCredentialsProvider(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
            .withEndpoint(endpoint)
            .build()

    @Bean
    fun jmsListenerContainerFactory(): DefaultJmsListenerContainerFactory {
        val factory = DefaultJmsListenerContainerFactory()
        factory.setConnectionFactory(this.connectionFactory)
        factory.setDestinationResolver(DynamicDestinationResolver())
        factory.setConcurrency("3-10")
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE)
        return factory
    }

    @Bean
    fun defaultJmsTemplate(): JmsTemplate {
        return JmsTemplate(this.connectionFactory)
    }
}
