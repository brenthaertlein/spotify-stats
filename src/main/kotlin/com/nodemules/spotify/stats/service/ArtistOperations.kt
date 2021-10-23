package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.data.Genre
import io.vavr.control.Either

interface ArtistOperations {
    fun getArtists(example: ArtistExample): Either<out Failure, List<Artist>>

    fun getArtists(ids: Collection<String>): Either<out Failure, List<Artist>>

    fun getArtist(id: String): Either<out Failure, Artist>

    fun getGenres(): Either<out Failure, Collection<String>>

    fun getTopGenres(limit: Long): Either<out Failure, List<Genre>>
}
