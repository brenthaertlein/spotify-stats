package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.ObjectMapper
import com.nodemules.spotify.stats.client.OAuth2Provider
import feign.RequestInterceptor
import feign.codec.ErrorDecoder
import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus

class SpotifyClientConfiguration(
    private val oAuth2Provider: OAuth2Provider
) {

    @Bean
    fun errorDecoder(objectMapper: ObjectMapper): ErrorDecoder = ErrorDecoder { _, response ->
        objectMapper
            .run { readValue(response.body().asInputStream(), SpotifyErrorResponse::class.java) }
            .run { SpotifyClientException(HttpStatus.valueOf(response.status()), error.message) }
    }

    @Bean
    fun requestInterceptor(): RequestInterceptor = RequestInterceptor { requestTemplate ->
        oAuth2Provider.getAccessToken(CLIENT_NAME)?.apply { requestTemplate.header("Authorization", "Bearer $tokenValue") }
        logger.info { "Making request to Spotify API: ${requestTemplate.url()}" }
    }

    companion object : KLogging() {
        private const val CLIENT_NAME = "spotify"
    }
}