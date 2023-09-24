package com.tuturing.api.configuration

import com.tuturing.api.security.CustomTokenEnhancer
import com.tuturing.api.security.CustomUserDetailsService
import com.tuturing.api.security.service.CustomClientDetailsService
import com.tuturing.api.security.service.OAuth2RequestValidator
import java.security.KeyPair
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore

@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfiguration(
    @Autowired private val userDetailsService: CustomUserDetailsService,
    @Autowired private val clientDetailsService: CustomClientDetailsService,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Qualifier("jwtPublicKey") @Autowired private val jwtPublicKey: RSAPublicKey,
    @Qualifier("jwtPrivateKey") @Autowired private val jwtPrivateKey: RSAPrivateKey
) : AuthorizationServerConfigurerAdapter() {
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Throws(Exception::class)
    override fun configure(oauthServer: AuthorizationServerSecurityConfigurer) {
        oauthServer.passwordEncoder(passwordEncoder)
        // figuring out if we can mix oauth with auth basic and enable a login form
        // oauthServer.allowFormAuthenticationForClients()
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
    }

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(clientDetailsService)
    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        defaultTokenServices.setSupportRefreshToken(true)
        defaultTokenServices.setClientDetailsService(clientDetailsService)
        return defaultTokenServices
    }

    @Throws(Exception::class)
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        val tokenEnhancerChain = TokenEnhancerChain()
        tokenEnhancerChain.setTokenEnhancers(listOf(tokenEnhancer(), accessTokenConverter()))
        endpoints
            .requestValidator(OAuth2RequestValidator())
            .tokenStore(tokenStore())
            .tokenEnhancer(tokenEnhancerChain)
            .authenticationManager(authenticationManager)
            .setClientDetailsService(clientDetailsService)
    }

    /*
    // apparently this is not needed for ClientDetailProvider to work
    @Bean
    @Autowired
    fun userApprovalHandler(tokenStore: TokenStore): TokenStoreUserApprovalHandler {
        val handler = TokenStoreUserApprovalHandler()
        handler.setTokenStore(tokenStore)
        handler.setRequestFactory(DefaultOAuth2RequestFactory(clientDetailsService))
        handler.setClientDetailsService(clientDetailsService)
        return handler
    }

    // apparently this is not needed for ClientDetailProvider to work
    @Bean
    @Autowired
    @Throws(java.lang.Exception::class)
    fun approvalStore(tokenStore: TokenStore): ApprovalStore {
        val store = TokenApprovalStore()
        store.setTokenStore(tokenStore)
        return store
    }

     */

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val duac = DefaultUserAuthenticationConverter()
        duac.setUserDetailsService(userDetailsService)

        val datc = DefaultAccessTokenConverter()
        datc.setUserTokenConverter(duac)

        val jatc = JwtAccessTokenConverter()
        jatc.accessTokenConverter = datc

        jatc.setKeyPair(KeyPair(jwtPublicKey, jwtPrivateKey))
        // jatc.setSigningKey(jwtKey)
        return jatc
    }

    @Bean
    fun tokenEnhancer(): TokenEnhancer {
        return CustomTokenEnhancer()
    }
}
