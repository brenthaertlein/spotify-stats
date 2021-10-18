package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.artist.SpotifyArtistClient
import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.persistence.repository.ArtistRepository
import io.vavr.control.Either
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ArtistService(
    private val spotifyArtistClient: SpotifyArtistClient,
    private val artistRepository: ArtistRepository,
    private val mongoTemplate: MongoTemplate
) : ArtistOperations {

    override fun getArtist(id: String): Either<out Failure, Artist> =
        artistRepository.findById(id)
            .map { Either.right<Failure, Artist>(it) }
            .orElseGet { Either.left(Failure.GenericFailure(HttpStatus.NOT_FOUND, "")) }
            .fold({ spotifyArtistClient.getArtist(id) }) { Either.right(it) }

    override fun getArtists(ids: Collection<String>): Either<out Failure, List<Artist>> = spotifyArtistClient.getArtists(ids)

    override fun getArtists(example: ArtistExample): Either<out Failure, List<Artist>> =
        example.run {
            Query().apply {
                name?.let { addCriteria(Criteria.where("name").regex(".*$it*", "i")) }
                genres?.let { addCriteria(Criteria.where("genres").`in`(it)) }
            }
        }
            .let { mongoTemplate.find(it, Artist::class.java) }
            .takeUnless { it.isEmpty() }
            ?.let { Either.right(it) }
            ?: Either.left(Failure.GenericFailure(HttpStatus.NOT_FOUND, "Unable to find artists for $example"))

    override fun getGenres(): Either<out Failure, Collection<String>> = artistRepository.findAll()
        .fold(mutableSetOf<String>()) { list, artist -> list.apply { artist.genres?.onEach { add(it) } } }
        .let { Either.right(it) }
}