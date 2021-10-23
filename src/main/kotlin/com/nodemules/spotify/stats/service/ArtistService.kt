package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.asNullableList
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.artist.SpotifyArtistClient
import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.data.Genre
import com.nodemules.spotify.stats.persistence.repository.ArtistRepository
import io.vavr.control.Either
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.sort
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
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
                name?.let { addCriteria(Criteria.where("name").regex(".*$it.*", "i")) }
                genres?.run {
                    addCriteria(Criteria().andOperator(map { Criteria.where("genres").regex(".*$it.*", "i") }))
                }
            }
        }
            .let { mongoTemplate.find(it, Artist::class.java) }
            .takeUnless { it.isEmpty() }
            ?.let { Either.right(it) }
            ?: Either.left(Failure.GenericFailure(HttpStatus.NOT_FOUND, "Unable to find artists for $example"))

    override fun getGenres(): Either<out Failure, Collection<String>> = artistRepository.findAll()
        .fold(mutableSetOf<String>()) { list, artist -> list.apply { artist.genres?.onEach { add(it) } } }
        .let { Either.right(it) }

    override fun getTopGenres(limit: Long, sort: String?): Either<out Failure, List<Genre>> =
        mutableListOf<AggregationOperation>()
            .apply {
                add(unwind("genres"))
                add(
                    group("genres")
                        .addToSet("\$\$ROOT").`as`("artists")
                        .min("popularity").`as`("minPopularity")
                        .max("popularity").`as`("maxPopularity")
                        .min("followers.total").`as`("minFollowers")
                        .max("followers.total").`as`("maxFollowers")
                )
                add(unwind("artists"))
                add(
                    project("_id")
                        .and(
                            ConditionalOperators.`when`(Criteria.where("artists.popularity").isEqualTo("\$maxPopularity"))
                                .then("\$artists").otherwise("\$\$REMOVE")
                        ).`as`("mostPopular")
                        .and(
                            ConditionalOperators.`when`(
                                Criteria().andOperator(
                                    Criteria.where("artists.popularity").isEqualTo("\$minPopularity"),
                                    Criteria.where("artists.popularity").ne("\$maxPopularity")
                                )
                            )
                                .then("\$artists").otherwise("\$\$REMOVE")
                        ).`as`("leastPopular")
                        .and(
                            ConditionalOperators.`when`(Criteria.where("artists.followers.total").isEqualTo("\$maxFollowers"))
                                .then("\$artists").otherwise("\$\$REMOVE")
                        ).`as`("mostFollowed")
                        .and(
                            ConditionalOperators.`when`(
                                Criteria().andOperator(
                                    Criteria.where("artists.followers.total").ne("\$maxFollowers"),
                                    Criteria.where("artists.followers.total").isEqualTo("\$minFollowers")
                                )
                            )
                                .then("\$artists").otherwise("\$\$REMOVE")
                        ).`as`("leastFollowed")

                )
                add(
                    group("_id")
                        .count().`as`("count")
                        .addToSet("mostPopular").`as`("mostPopular")
                        .addToSet("leastPopular").`as`("leastPopular")
                        .addToSet("mostFollowed").`as`("mostFollowed")
                        .addToSet("leastFollowed").`as`("leastFollowed")
                )
                add(
                    sort?.run {
                        val (field, direction) = split(",").asNullableList(2)
                        val sortDirection = when (direction) {
                            "asc" -> Sort.Direction.ASC; else -> Sort.Direction.DESC
                        }
                        when (field) {
                            "popularity" -> sort(sortDirection, "mostPopular.popularity")
                            "followers" -> sort(sortDirection, "mostFollowed.followers.total")
                            else -> null
                        }
                    } ?: sort(Sort.Direction.DESC, "count")
                )
                add(limit(limit))
                add(
                    project().andExpression("_id").`as`("genre")
                        .and("mostPopular").arrayElementAt(0).`as`("mostPopular")
                        .and("leastPopular").arrayElementAt(0).`as`("leastPopular")
                        .and("mostFollowed").arrayElementAt(0).`as`("mostFollowed")
                        .and("leastFollowed").arrayElementAt(0).`as`("leastFollowed")
                        .andInclude("count")
                )
            }
            .run { this.toTypedArray() }
            .run { mongoTemplate.aggregate(Aggregation.newAggregation(*this), Artist::class.java, Genre::class.java) }
            .let { Either.right(it.mappedResults) }
}
