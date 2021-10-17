package com.nodemules.spotify.stats.client.spotify

import io.vavr.control.Either
import mu.KLogging
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
    fallbackFactory = SpotifyBrowseFeignClient.FeignClientFallbackFactory::class
)
interface SpotifyBrowseFeignClient {

    @GetMapping("/categories")
    fun getCategories(@SpringQueryMap query: PageableQuery = PageableQuery()): Either<SpotifyErrorResponse, CategoriesResponse>

    @GetMapping("/categories/{id}/playlists")
    fun getCategoryPlaylist(@PathVariable id: String): Either<SpotifyErrorResponse, CategoryPlaylistsResponse>

    @Component
    class FeignClientFallbackFactory : FallbackFactory<SpotifyBrowseFeignClient> {
        override fun create(cause: Throwable) = object : SpotifyBrowseFeignClient {
            override fun getCategories(query: PageableQuery): Either<SpotifyErrorResponse, CategoriesResponse> {
                logger.error(cause) { "Error getting categories with $query" }
                return Either.left(SpotifyErrorResponse(error = cause.cause.toSpotifyError()))
            }

            override fun getCategoryPlaylist(id: String): Either<SpotifyErrorResponse, CategoryPlaylistsResponse> {
                logger.error(cause) { "Error getting playlists for Category(id=$id)" }
                return Either.left(SpotifyErrorResponse(error = cause.cause.toSpotifyError()))
            }
        }

        companion object : KLogging() {
            private fun Throwable?.toSpotifyError() = when (this) {
                is SpotifyClientException -> SpotifyErrorResponse.SpotifyError(status = httpStatus.value(), message = message)
                else -> SpotifyErrorResponse.SpotifyError(status = 500, message = this?.cause?.message ?: "")
            }
        }
    }
}