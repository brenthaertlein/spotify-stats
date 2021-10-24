package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.artist.SpotifyArtistClient
import com.nodemules.spotify.stats.client.spotify.artist.SpotifyArtistFeignClient
import com.nodemules.spotify.stats.client.spotify.tracks.CacheableSpotifyTracksClient
import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.data.Genre
import com.nodemules.spotify.stats.page
import com.nodemules.spotify.stats.persistence.repository.ArtistRepository
import io.vavr.control.Either
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.limit
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.skip
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
    private val spotifyArtistFeignClient: SpotifyArtistFeignClient,
    private val spotifyArtistClient: SpotifyArtistClient,
    private val cacheableSpotifyTracksClient: CacheableSpotifyTracksClient,
    private val artistRepository: ArtistRepository,
    private val mongoTemplate: MongoTemplate
) : ArtistOperations {

    @Cacheable("artist", unless = "#result.isLeft")
    override fun getArtist(id: String): Either<out Failure, Artist> = spotifyArtistClient.getArtist(id)

    override fun getArtists(ids: Collection<String>): Either<out Failure, List<Artist>> = spotifyArtistClient.getArtists(ids)

    override fun getArtists(example: ArtistExample, pageable: Pageable): Either<out Failure, Page<Artist>> =
        example.run {
            Query().apply {
                name?.let { addCriteria(Criteria.where("name").regex(".*$it.*", "i")) }
                genres?.run {
                    addCriteria(Criteria().andOperator(map { Criteria.where("genres").regex(".*$it.*", "i") }))
                }
            }
        }
            .let { Either.right(mongoTemplate.page(it, Artist::class.java, pageable)) }

    override fun getGenres(): Either<out Failure, Collection<String>> = artistRepository.findAll()
        .fold(mutableSetOf<String>()) { list, artist -> list.apply { artist.genres?.onEach { add(it) } } }
        .let { Either.right(it) }

    override fun getTopGenres(pageable: Pageable): Either<out Failure, Page<Genre>> =
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
                    project().andExpression("_id").`as`("genre")
                        .and("mostPopular").arrayElementAt(0).`as`("mostPopular")
                        .and("leastPopular").arrayElementAt(0).`as`("leastPopular")
                        .and("mostFollowed").arrayElementAt(0).`as`("mostFollowed")
                        .and("leastFollowed").arrayElementAt(0).`as`("leastFollowed")
                        .andInclude("count")
                )
                add(
                    pageable.sort
                        .takeUnless { it.isEmpty }
                        ?.run {
                            map {
                                val sortDirection = getOrderFor(it.property)?.direction ?: Sort.Direction.DESC
                                when (it.property) {
                                    "popularity" -> Sort.Order(sortDirection, "mostPopular.popularity")
                                    "followers" -> Sort.Order(sortDirection, "mostFollowed.followers.total")
                                    "count" -> Sort.Order(sortDirection, "count")
                                    else -> Sort.Order(Sort.Direction.DESC, "count")
                                }
                            }
                        }
                        ?.let { sort(Sort.by(it.toList())) }
                        ?: sort(Sort.by(Sort.Order(Sort.Direction.DESC, "count")))
                )
                add(skip(pageable.offset))
                add(limit(pageable.pageSize.toLong()))
            }
            .run {
                mongoTemplate.page(newAggregation(Artist::class.java, *toTypedArray()), pageable, Genre::class.java) {
                    newAggregation(Artist::class.java, unwind("genres"), group("genres"), Aggregation.count().`as`("count"))
                }
            }
            .let { Either.right(it) }

    @Cacheable("spotify.artist.top-tracks", unless = "#result.isLeft")
    override fun getArtistTopTracks(artistId: String): Either<out Failure, List<Track>> =
        spotifyArtistFeignClient.getTopTracks(artistId).map { (tracks) -> tracks.onEach { cacheableSpotifyTracksClient.put(it) } }

    override fun findByName(name: String): Either<out Failure, Artist> =
        artistRepository.findByName(name).toEither { Failure.GenericFailure(HttpStatus.NOT_FOUND, "Artist [$name] not found") }

    override fun findByGenre(genres: List<String>): Either<out Failure, List<Artist>> =
        Query()
            .apply { addCriteria(Criteria().andOperator(genres.map { Criteria.where("genres").regex(".*$it.*", "i") })) }
            .run { mongoTemplate.page(this, Artist::class.java, PageRequest.of(0, 50)) }
            .let { Either.right(it.content) }
}
