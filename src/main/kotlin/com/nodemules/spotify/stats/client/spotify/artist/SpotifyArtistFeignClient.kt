package com.nodemules.spotify.stats.client.spotify.artist

import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.FeignClientExtensions
import com.nodemules.spotify.stats.client.spotify.SpotifyClientConfiguration
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.client.spotify.artist.SpotifyArtistFeignClient.FeignClientFallbackFactory
import io.vavr.control.Either
import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "spotifyArtistFeignClient",
    url = "\${feign.client.config.spotify.host}/v1/artists",
    configuration = [SpotifyClientConfiguration::class],
    fallbackFactory = FeignClientFallbackFactory::class
)
interface SpotifyArtistFeignClient {

    @GetMapping("/{id}")
    fun getArtist(@PathVariable id: String): Either<SpotifyErrorResponse, Artist>

    @GetMapping("")
    @CollectionFormat(feign.CollectionFormat.CSV)
    fun getArtists(@RequestParam ids: Collection<String>): Either<SpotifyErrorResponse, ArtistsResponse>

    @GetMapping("/{id}/top-tracks")
    fun getTopTracks(@PathVariable id: String, @RequestParam market: String = "US"): Either<SpotifyErrorResponse, TopTracksResponse>

    @Component
    class FeignClientFallbackFactory : FallbackFactory<SpotifyArtistFeignClient> {
        override fun create(cause: Throwable) = object : SpotifyArtistFeignClient {
            override fun getArtist(id: String): Either<SpotifyErrorResponse, Artist> =
                cause.asFallback { "Unable to get spotify:artist:$id" }

            override fun getArtists(ids: Collection<String>): Either<SpotifyErrorResponse, ArtistsResponse> =
                cause.asFallback { "Unable to get ${ids.joinToString(", ") { "spotify:artist:$it" }}" }

            override fun getTopTracks(id: String, market: String): Either<SpotifyErrorResponse, TopTracksResponse> =
                cause.asFallback { "Unable to get top tracks for spotify:artist:$id in market=$market" }
        }

        companion object : FeignClientExtensions()
    }
}
