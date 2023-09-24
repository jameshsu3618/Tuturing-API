package com.tuturing.api.security.service

import com.tuturing.api.security.entity.OAuthClient
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.ClientDetails

class CustomClientDetails(
    val oAuthClient: OAuthClient,
    val accessTokenValidityTime: Int,
    val refreshTokenValidityTime: Int,
    val grantTypes: MutableSet<String>
) : ClientDetails {
    override fun isSecretRequired(): Boolean {
        return true
    }

    override fun getAdditionalInformation(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>()
    }

    override fun getAccessTokenValiditySeconds(): Int {
        return accessTokenValidityTime
    }

    override fun getResourceIds(): MutableSet<String> {
        return mutableSetOf<String>()
    }

    override fun getClientId(): String {
        // client id is case sensitive, forcing the uppercase
        return oAuthClient.id.toString().toLowerCase()
    }

    override fun isAutoApprove(scope: String?): Boolean {
        return true
    }

    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
        return mutableListOf<GrantedAuthority>()
    }

    override fun getRefreshTokenValiditySeconds(): Int {
        return refreshTokenValidityTime
    }

    override fun getClientSecret(): String {
        return oAuthClient.secret
    }

    override fun getRegisteredRedirectUri(): MutableSet<String> {
        return mutableSetOf<String>()
    }

    override fun isScoped(): Boolean {
        return true
    }

    override fun getScope(): MutableSet<String> {
        return oAuthClient.scopes.map { it.name }.toMutableSet()
    }

    override fun getAuthorizedGrantTypes(): MutableSet<String> {
        return grantTypes
    }
}
