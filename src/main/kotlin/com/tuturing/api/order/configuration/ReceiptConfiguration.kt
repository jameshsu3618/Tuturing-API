package com.tuturing.api.order.configuration

import com.tuturing.api.order.valueobject.ReceiptParams
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReceiptConfiguration(
    @Qualifier("jwtPublicKey") @Autowired private val jwtPublicKey: RSAPublicKey,
    @Qualifier("jwtPrivateKey") @Autowired private val jwtPrivateKey: RSAPrivateKey,
    @NotEmpty @Value("\${tuturing.security.jwt.issuer}") private val jwtIssuer: String,
    @NotEmpty @Value("\${tuturing.orders.receipt-expiration-time}") private val receiptExpirationTime: Int,
    @NotEmpty @Value("\${tuturing.api-base-url}") private val apiBaseUrl: String

) {
    @Bean
    fun receiptParams(): ReceiptParams = ReceiptParams(
        apiBaseUrl,
        jwtPublicKey,
        jwtPrivateKey,
        jwtIssuer,
        receiptExpirationTime
    )
}
