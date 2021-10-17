package com.nodemules.spotify.stats.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository

@Configuration
class OAuth2Config {

    @Bean
    fun oAuth2AuthorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        oAuth2AuthorizedClientService: OAuth2AuthorizedClientService
    ): OAuth2AuthorizedClientManager =
        AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService)
}