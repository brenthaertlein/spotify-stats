package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.PageableQuery
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["spotify.browse"])
class CacheableSpotifyBrowseClient(
    private val spotifyBrowseFeignClient: SpotifyBrowseFeignClient
) : SpotifyBrowseClient {

    @Cacheable(key = "#root.methodName", unless = "#result.isLeft")
    override fun getCategories(): Either<SpotifyErrorResponse, CategoriesResponse> =
        spotifyBrowseFeignClient.getCategories(PageableQuery(limit = 50))

    @Cacheable(unless = "#result.isLeft")
    override fun getCategoryPlaylist(id: String) = spotifyBrowseFeignClient.getCategoryPlaylist(id)
}