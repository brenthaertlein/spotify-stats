package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.artist.SpotifyArtistClient
import com.nodemules.spotify.stats.persistence.repository.ArtistRepository
import io.vavr.control.Either
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ArtistService(
    private val spotifyArtistClient: SpotifyArtistClient,
    private val artistRepository: ArtistRepository
) : ArtistOperations {

    override fun getArtist(id: String): Either<out Failure, Artist> =
        artistRepository.findById(id)
            .map { Either.right<Failure, Artist>(it) }
            .orElseGet { Either.left(Failure.GenericFailure(HttpStatus.NOT_FOUND, "")) }
            .fold({ spotifyArtistClient.getArtist(id) }) { Either.right(it) }

    override fun getArtists(ids: Collection<String>): Either<out Failure, List<Artist>> = spotifyArtistClient.getArtists(ids)

    override fun getArtists(): Either<out Failure, List<Artist>> = Either.right(artistRepository.findAll())

    override fun getGenres(): Either<out Failure, Collection<String>> = artistRepository.findAll()
        .fold(mutableSetOf<String>()) { list, artist -> list.apply { artist.genres?.onEach { add(it) } } }
        .let { Either.right(it) }
}