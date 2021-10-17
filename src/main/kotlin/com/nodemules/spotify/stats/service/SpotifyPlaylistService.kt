package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.client.StringCachingClient
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.playlist.SpotifyPlaylistClient
import io.vavr.control.Either
import org.springframework.stereotype.Service

@Service
class SpotifyPlaylistService(
    private val cacheableSpotifyPlaylistClient: SpotifyPlaylistClient,
    private val cacheableSpotifyTracksClient: StringCachingClient<Track>
) : SpotifyPlaylistOperations {

    override fun getPlaylistTracks(playlistId: String): Either<SpotifyErrorResponse, List<Track>> =
        cacheableSpotifyPlaylistClient.getPlaylistTracks(playlistId)
            .map { (items) -> items.mapNotNull { cacheableSpotifyTracksClient.put(it.track).orNull } }
}