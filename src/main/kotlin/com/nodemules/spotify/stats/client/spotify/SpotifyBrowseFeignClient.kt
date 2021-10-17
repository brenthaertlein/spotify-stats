package com.nodemules.spotify.stats.client.spotify

import mu.KLogging
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "spotifyBrowseFeignClient",
    url = "https://api.spotify.com/v1/browse",
    configuration = [SpotifyClientConfiguration::class],
    fallbackFactory = SpotifyBrowseFeignClient.FeignClientFallbackFactory::class
)
interface SpotifyBrowseFeignClient : SpotifyBrowseClient {

    @GetMapping("/categories")
    override fun getCategories(pageable: Pageable): CategoriesResponse?

    class FeignClientFallbackFactory : FallbackFactory<SpotifyBrowseFeignClient> {
        override fun create(cause: Throwable?) = object : SpotifyBrowseFeignClient {
            override fun getCategories(pageable: Pageable): CategoriesResponse? {
                logger.error(cause) { "Error getting stuff" }
                return null
            }
        }

        companion object : KLogging()
    }
}