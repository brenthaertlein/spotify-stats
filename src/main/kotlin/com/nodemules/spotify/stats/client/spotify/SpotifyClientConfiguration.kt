package com.nodemules.spotify.stats.client.spotify

import com.nodemules.spotify.stats.client.OAuth2Provider
import feign.RequestInterceptor
import mu.KLogging
import org.springframework.context.annotation.Bean

class SpotifyClientConfiguration(
    private val oAuth2Provider: OAuth2Provider
) {

    @Bean
    fun requestInterceptor(): RequestInterceptor = RequestInterceptor { requestTemplate ->
        oAuth2Provider.getAccessToken(CLIENT_NAME)?.apply { requestTemplate.header("Authorization", "Bearer $tokenValue") }
        logger.info { "Making request to Spotify API: ${requestTemplate.url()}" }
    }

    companion object : KLogging() {
        private const val CLIENT_NAME = "spotify"
    }
}