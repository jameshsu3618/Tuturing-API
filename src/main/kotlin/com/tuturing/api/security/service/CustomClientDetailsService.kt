package com.tuturing.api.security.service

import com.tuturing.api.security.repository.OAuthClientRepository
import java.util.*
import javax.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationException
import org.springframework.security.oauth2.provider.NoSuchClientException
import org.springframework.stereotype.Service

@Service
class CustomClientDetailsService(
    @Autowired val oAuthClientRepository: OAuthClientRepository,

    @NotEmpty @Value("\${tuturing.security.oauth.access-token-validity-seconds:3600}")
    val accessTokenValidityTime: Int,

    @NotEmpty @Value("\${tuturing.security.oauth.refresh-token-validity-seconds:2592000}")
    val refreshTokenValidityTime: Int,

    @NotEmpty @Value("\${tuturing.security.oauth.authorized-grant-types:password,refresh_token}")
    val grantTypes: List<String>
) : ClientDetailsService {
    @Throws(ClientRegistrationException::class)
    override fun loadClientByClientId(clientId: String): ClientDetails {
        val entity = oAuthClientRepository.findById(UUID.fromString(clientId))

        if (entity.isEmpty) {
            throw NoSuchClientException(String.format("Client {id} does not exist", clientId))
        } else {
            val client = CustomClientDetails(entity.get(), accessTokenValidityTime, refreshTokenValidityTime, grantTypes.toMutableSet())

            return client
        }
    }
}
