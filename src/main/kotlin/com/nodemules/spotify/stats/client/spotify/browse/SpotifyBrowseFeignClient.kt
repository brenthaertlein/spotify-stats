package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.FeignClientExtensions
import com.nodemules.spotify.stats.client.spotify.PageableQuery
import com.nodemules.spotify.stats.client.spotify.SpotifyClientConfiguration
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.client.spotify.browse.SpotifyBrowseFeignClient.FeignClientFallbackFactory
import io.vavr.control.Either
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "spotifyBrowseFeignClient",
    url = "https://api.spotify.com/v1/browse",
    configuration = [SpotifyClientConfiguration::class],
    fallbackFactory = FeignClientFallbackFactory::class
)
interface SpotifyBrowseFeignClient {

    @GetMapping("/categories")
    fun getCategories(@SpringQueryMap query: PageableQuery = PageableQuery()): Either<SpotifyErrorResponse, CategoriesResponse>

    @GetMapping("/categories/{id}/playlists")
    fun getCategoryPlaylist(@PathVariable id: String): Either<SpotifyErrorResponse, CategoryPlaylistsResponse>

    @Component
    class FeignClientFallbackFactory : FallbackFactory<SpotifyBrowseFeignClient> {
        override fun create(cause: Throwable) = object : SpotifyBrowseFeignClient {
            override fun getCategories(query: PageableQuery): Either<SpotifyErrorResponse, CategoriesResponse> =
                cause.asFallback { "Error getting categories with $query" }

            override fun getCategoryPlaylist(id: String): Either<SpotifyErrorResponse, CategoryPlaylistsResponse> =
                cause.asFallback { "Error getting playlists for Category(id=$id)" }
        }

        companion object : FeignClientExtensions()
    }
}
