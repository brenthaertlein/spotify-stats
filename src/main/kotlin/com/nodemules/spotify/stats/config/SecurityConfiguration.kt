package com.nodemules.spotify.stats.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val corsConfigurationProperties: CorsConfigurationProperties
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors().and().authorizeRequests().anyRequest().permitAll()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? =
        CorsConfiguration()
            .apply {
                allowedOrigins = corsConfigurationProperties.allowedOrigins
                allowedMethods = listOf("GET", "POST")
            }
            .let {
                UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", it) }
            }
}