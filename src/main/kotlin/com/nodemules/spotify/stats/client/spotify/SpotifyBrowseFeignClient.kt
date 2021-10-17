package com.nodemules.spotify.stats.client.spotify

import mu.KLogging
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "spotify",
    url = "https://api.spotify.com/v1/browse",
    configuration = [SpotifyClientConfiguration::class],
    fallbackFactory = SpotifyBrowseFeignClient.FeignClientFallbackFactory::class
)
interface SpotifyBrowseFeignClient {

    @GetMapping("/categories")
    fun getCategories(): CategoriesResponse?

    class FeignClientFallbackFactory : FallbackFactory<SpotifyBrowseFeignClient> {
        override fun create(cause: Throwable?) = object : SpotifyBrowseFeignClient {
            override fun getCategories(): CategoriesResponse? {
                logger.error(cause) { "Error getting stuff" }
                return null
            }
        }

        companion object : KLogging()
    }
}