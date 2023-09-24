package com.tuturing.api.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices

@Configuration
@EnableResourceServer
class ResourceServerConfiguration(
    @Autowired private val tokenServices: DefaultTokenServices
) : ResourceServerConfigurerAdapter() {

    override fun configure(config: ResourceServerSecurityConfigurer) {
        // we can re-use the DefaultTokenServices created in AuthorizationServerConfiguration
        config.tokenServices(tokenServices)
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // Disable CSRF but enable CORS
        // The OAuth2 implementation enables CORS
        // Enabling CORS is needed just to be able to manipulate that filter
        // and disable for /oauth/token
        http.csrf().disable().cors()
            .and()
            .anonymous() // enable anonymous role, it still requires api client to be authenticated
            .and()
            .authorizeRequests()
            // CORS
            // .antMatchers(HttpMethod.OPTIONS).permitAll() // seems to be not needed anymore, OAuth2 CORS thing

            // ERROR responses
            .antMatchers("/error").permitAll() // enable error responses for unauthenticated requests

            // HEALTCHECKS
            // TODO: enable for specific users only
            .antMatchers("/actuator/**").permitAll()

            // SWAGGER
            .antMatchers("/swagger-ui.html").permitAll()
            .antMatchers("/webjars/springfox-swagger-ui/**").permitAll()
            .antMatchers("/null/swagger-resources/**").permitAll()
            .antMatchers("/null/swagger-resources").permitAll()
            .antMatchers("/swagger-resources/**").permitAll()
            .antMatchers("/swagger-resources").permitAll()
            .antMatchers("/v2/api-docs").permitAll()
            .antMatchers("/swagger-ui.html").permitAll()

            // PUBLIC API
            // public API endpoints are exposed via WebSecurityConfiguration
            // by ignoring Spring Security
            .anyRequest().authenticated()
    }
}
