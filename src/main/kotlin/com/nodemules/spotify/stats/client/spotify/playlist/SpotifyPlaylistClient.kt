package com.nodemules.spotify.stats.client.spotify.playlist

import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either

interface SpotifyPlaylistClient {
    fun getPlaylistTracks(playlistId: String): Either<SpotifyErrorResponse, PageableResponse<TrackItem>>
}