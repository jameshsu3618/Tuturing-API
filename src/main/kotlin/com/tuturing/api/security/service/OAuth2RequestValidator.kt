package com.tuturing.api.security.service

import org.springframework.security.oauth2.common.exceptions.InvalidScopeException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.TokenRequest

/**
 * Fixes Spring OAuth2 bug. By default, Spring OAuth2 will use the scope from authentication request
 * This way a client can send any scope and will be granted that scope even if no such scope is assigned to a client
 */
class OAuth2RequestValidator : org.springframework.security.oauth2.provider.OAuth2RequestValidator {
    @Throws(InvalidScopeException::class)
    override fun validateScope(authorizationRequest: AuthorizationRequest, client: ClientDetails) {
        this.validateScope(authorizationRequest.scope, client.scope, client.isScoped)
    }

    @Throws(InvalidScopeException::class)
    override fun validateScope(tokenRequest: TokenRequest, client: ClientDetails) {
        this.validateScope(tokenRequest.scope, client.scope, client.isScoped)
    }

    /**
     * Invalidate oauth2 request if the requested scope is not on the list of allowed scopes.
     *
     * RFC-6749
     * If the client omits the scope parameter when requesting
     * authorization, the authorization server MUST either process the
     * request using a pre-defined default value or fail the request
     * indicating an invalid scope.
     */
    private fun validateScope(requestScopes: Set<String>, clientScopes: Set<String>, isScoped: Boolean) {
        requestScopes.forEach { scope ->
            if (!clientScopes.contains(scope)) {
                throw InvalidScopeException("Invalid scope: $scope isScoped $isScoped", clientScopes)
            }
        }
    }
}
