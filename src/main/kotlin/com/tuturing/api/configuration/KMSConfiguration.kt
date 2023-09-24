package com.tuturing.api.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.kms.AWSKMSClientBuilder
import com.tuturing.api.paymentmethod.api.KmsClient
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KMSConfiguration(
    @NotEmpty @Value("\${tuturing.aws.kms.region}") var region: String,
    @NotEmpty @Value("\${tuturing.aws.kms.access-key}") var accessKey: String,
    @NotEmpty @Value("\${tuturing.aws.kms.secret-key}") var secretKey: String,
    @NotEmpty @Value("\${tuturing.aws.kms.endpoint}") var endpoint: String,
    @NotEmpty @Value("\${tuturing.aws.kms.key-id}") var keyId: String
) {
    // TODO How to implement automatic key rotation with kms-managed key that's passed in as config param
    @Bean
    fun kmsClient(): KmsClient {
        return KmsClient(AWSKMSClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build(), keyId)
    }
}
