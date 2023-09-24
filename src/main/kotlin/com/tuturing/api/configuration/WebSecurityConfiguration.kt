package com.tuturing.api.configuration

import com.tuturing.api.security.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class WebSecurityConfiguration(
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private var userDetailsService: CustomUserDetailsService
) : WebSecurityConfigurerAdapter() {

    @Autowired
    @Throws(Exception::class)
    fun globalUserDetails(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder)
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        // HttpSecurity configured by ResourceServer
        // For some reason configuring it hear breaks some things
        // e.g. CORS filter works but anonymous access not, and vice-versa
        // Except for disabling csrf - it saves a lot of setup work
        // for writing controller unit tests
        http.csrf().disable()
    }

    @Throws(java.lang.Exception::class)
    override fun configure(web: WebSecurity) {
        // expose public API endpoints
        // by ignoring spring security
        web.ignoring()
                .antMatchers("/*")
//                .antMatchers("/user/password/reset")
//                .antMatchers("/user/verify")
//                .antMatchers("/company/register")
//                .antMatchers("/company/verify")
//                .antMatchers("/company/resend-invitation")
//                .antMatchers("/orders/*/receipt")
//                .antMatchers("/rapid/notifications")
    }
}
