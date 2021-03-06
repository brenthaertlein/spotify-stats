package com.nodemules.spotify.stats.client.spotify.artist

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either

interface SpotifyArtistClient {
    fun getArtist(id: String): Either<out Failure, Artist>

    fun getArtists(ids: Collection<String>): Either<SpotifyErrorResponse, List<Artist>>
}
