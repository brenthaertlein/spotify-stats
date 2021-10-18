package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import io.vavr.control.Either

interface ArtistOperations {
    fun getArtists(): Either<out Failure, List<Artist>>

    fun getArtists(ids: Collection<String>): Either<out Failure, List<Artist>>

    fun getArtist(id: String): Either<out Failure, Artist>

    fun getGenres(): Either<out Failure, Collection<String>>
}