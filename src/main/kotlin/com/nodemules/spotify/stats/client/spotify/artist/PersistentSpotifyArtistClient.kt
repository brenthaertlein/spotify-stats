package com.nodemules.spotify.stats.client.spotify.artist

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.flatMapLeft
import com.nodemules.spotify.stats.persistence.repository.ArtistRepository
import io.vavr.control.Either
import io.vavr.kotlin.option
import mu.KLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class PersistentSpotifyArtistClient(
    private val spotifyArtistFeignClient: SpotifyArtistFeignClient,
    private val artistRepository: ArtistRepository
) : SpotifyArtistClient {

    override fun getArtist(id: String): Either<out Failure, Artist> =
        artistRepository.findByIdOrNull(id).option().toEither { Failure.GenericFailure(HttpStatus.NOT_FOUND, "Artist $id not found") }
            .flatMapLeft { spotifyArtistFeignClient.getArtist(id).map { artistRepository.save(it) }.mapLeft { it.copy() } }

    override fun getArtists(ids: Collection<String>): Either<SpotifyErrorResponse, List<Artist>> =
        spotifyArtistFeignClient.getArtists(ids.distinct().take(50))
            .map { artistRepository.saveAll(it.artists) }

    companion object : KLogging()
}
