package com.nodemules.spotify.stats.client.spotify

import io.vavr.control.Either
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["browse"])
class CacheableSpotifyBrowseClient(
    private val spotifyBrowseFeignClient: SpotifyBrowseFeignClient
) : SpotifyBrowseClient {

    @Cacheable(key = "#root.methodName", unless = "#result.isLeft")
    override fun getCategories(pageable: Pageable): Either<SpotifyErrorResponse, CategoriesResponse> = spotifyBrowseFeignClient.getCategories()
}