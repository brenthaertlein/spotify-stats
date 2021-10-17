package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.client.spotify.Track
import io.vavr.control.Either

interface SpotifyPlaylistOperations {
    fun getPlaylistTracks(playlistId: String): Either<SpotifyErrorResponse, List<Track>>
}