package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.data.Genre
import io.vavr.control.Either
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ArtistOperations {
    fun getArtists(example: ArtistExample, pageable: Pageable): Either<out Failure, Page<Artist>>

    fun getArtists(ids: Collection<String>): Either<out Failure, List<Artist>>

    fun getArtist(id: String): Either<out Failure, Artist>

    fun getGenres(): Either<out Failure, Collection<String>>

    fun getTopGenres(pageable: Pageable): Either<out Failure, Page<Genre>>
}
