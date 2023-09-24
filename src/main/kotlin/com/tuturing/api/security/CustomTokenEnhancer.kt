package com.tuturing.api.security

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer

/**
 * CustomTokenEnhancer is an example of adding more fields to the Oauth2 token
 */
class CustomTokenEnhancer : TokenEnhancer {
    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        val principal = authentication.principal

        if (principal is CustomUserDetails) {
            val additionalInfo: MutableMap<String, Any> = HashMap()
            val id = principal.user.id
            if (id != null) additionalInfo["userId"] = id

            (accessToken as DefaultOAuth2AccessToken).additionalInformation = additionalInfo
        }

        return accessToken
    }
}
