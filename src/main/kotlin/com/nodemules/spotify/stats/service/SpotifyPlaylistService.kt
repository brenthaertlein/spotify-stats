package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.client.StringCachingClient
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.playlist.SpotifyPlaylistClient
import org.springframework.stereotype.Service

@Service
class SpotifyPlaylistService(
    private val cacheableSpotifyPlaylistClient: SpotifyPlaylistClient,
    private val cacheableSpotifyTracksClient: StringCachingClient<Track>
) : SpotifyPlaylistOperations {

    override fun getPlaylistTracks(playlistId: String) = cacheableSpotifyPlaylistClient.getPlaylistTracks(playlistId)
        .map { (items) -> items.mapNotNull { cacheableSpotifyTracksClient.put(it.track).orNull } }
}