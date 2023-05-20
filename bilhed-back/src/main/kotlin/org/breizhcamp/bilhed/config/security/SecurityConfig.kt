package org.breizhcamp.bilhed.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun jwtAuthenticationConverter() = JwtAuthenticationConverter().apply {
        setPrincipalClaimName("preferred_username")
        setJwtGrantedAuthoritiesConverter(JwtAuthoritiesConverter())
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.addFilterAfter(UserMDCFilter(), SwitchUserFilter::class.java)

        http.oauth2ResourceServer().jwt()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.authorizeHttpRequests()
            .requestMatchers("/admin/**").hasRole("BILHED")
            .requestMatchers("/api/**").permitAll()
            .anyRequest().denyAll()

        http.csrf().disable()

        return http.build()
    }

}