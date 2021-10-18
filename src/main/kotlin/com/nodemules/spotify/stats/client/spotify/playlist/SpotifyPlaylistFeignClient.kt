package com.nodemules.spotify.stats.client.spotify.playlist

import com.nodemules.spotify.stats.client.spotify.FeignClientExtensions
import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.SpotifyClientConfiguration
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either
import org.springframework.cloud.openfeign.FallbackFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "spotifyPlaylistFeignClient",
    url = "https://api.spotify.com/v1/playlists",
    configuration = [SpotifyClientConfiguration::class],
    fallbackFactory = SpotifyPlaylistFeignClient.FeignClientFallbackFactory::class
)
interface SpotifyPlaylistFeignClient {

    @GetMapping("/{id}/tracks")
    fun getPlaylistTracks(@PathVariable id: String): Either<SpotifyErrorResponse, PageableResponse<TrackItem>>

    @Component
    class FeignClientFallbackFactory : FallbackFactory<SpotifyPlaylistFeignClient> {
        override fun create(cause: Throwable) = object : SpotifyPlaylistFeignClient {
            override fun getPlaylistTracks(id: String): Either<SpotifyErrorResponse, PageableResponse<TrackItem>> =
                cause.asFallback { "Unable to get tracks for Playlist(id=$id)" }
        }

        companion object : FeignClientExtensions()
    }
}