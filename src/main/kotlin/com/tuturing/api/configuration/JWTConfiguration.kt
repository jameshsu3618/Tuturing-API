package com.tuturing.api.configuration

import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JWTConfiguration(
    @NotEmpty @Value("\${tuturing.security.jwt.public-key}") val publicKey: String,
    @NotEmpty @Value("\${tuturing.security.jwt.private-key}") val privateKey: String
) {
    var kf = KeyFactory.getInstance("RSA")

    @Bean("jwtPublicKey")
    fun jwtPublicKey(): RSAPublicKey {
        val publicKeyContent = publicKey
            .replace("\\n".toRegex(), "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")

        val keySpecX509 = X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent))
        return kf.generatePublic(keySpecX509) as RSAPublicKey
    }

    @Bean("jwtPrivateKey")
    fun jwtPrivateKey(): RSAPrivateKey {
        val privateKeyContent = privateKey
            .replace("\\n".toRegex(), "")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")

        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent))
        return kf.generatePrivate(keySpecPKCS8) as RSAPrivateKey
    }
}
