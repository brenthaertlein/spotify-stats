package com.nodemules.spotify.stats.client.spotify.playlist

import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["spotify.playlist"])
class CacheableSpotifyPlaylistClient(
    private val spotifyPlaylistFeignClient: SpotifyPlaylistFeignClient,
) : SpotifyPlaylistClient {

    @Cacheable(unless = "#result.isLeft")
    override fun getPlaylistTracks(playlistId: String): Either<SpotifyErrorResponse, PageableResponse<TrackItem>> =
        spotifyPlaylistFeignClient.getPlaylistTracks(playlistId)

}