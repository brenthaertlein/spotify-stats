package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.Failure.GenericFailure
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.tracks.CacheableSpotifyTracksClient
import com.nodemules.spotify.stats.data.TrackExample
import com.nodemules.spotify.stats.flatMapLeft
import com.nodemules.spotify.stats.narrowFlatMap
import com.nodemules.spotify.stats.sample
import io.vavr.control.Either
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class SpotifyTrackService(
    private val spotifyBrowseService: SpotifyBrowseOperations,
    private val spotifyPlaylistService: SpotifyPlaylistOperations,
    private val cacheableSpotifyTracksClient: CacheableSpotifyTracksClient,
    private val artistService: ArtistService
) : SpotifyTrackOperations {

    private val wellKnownCategories = mutableSetOf("pop", "punk", "rock", "rnb")

    override fun getRandomTrack(trackExample: TrackExample): Either<out Failure, Track> =
        run {
            val (artist, genres, category) = trackExample
            artist?.let { artistService.findByName(artist).narrowFlatMap { artistService.getArtistTopTracks(it.id) }.map { it.sample() } }
                ?: genres?.let {
                    artistService.findByGenre(genres)
                        .narrowFlatMap {
                            logger.info { "Found ${it.size} artists for genres [$genres]" }
                            it.sample().toEither { FAILURE_NOT_FOUND }
                        }
                        .narrowFlatMap { (id, name) ->
                            logger.info { "Getting top tracks for artist $name" }
                            artistService.getArtistTopTracks(id)
                                .map {
                                    logger.info { "Found ${it.size} tracks for artist [$name]" }
                                    it.sample()
                                }
                        }
                }
                ?: category?.let { getCompletelyRandomTrack(it.lowercase()) }
                ?: getCompletelyRandomTrack()
        }
            .flatMapLeft {
                logger.warn { "Unable to find a random track because [${it.message}], trying cache" }
                cacheableSpotifyTracksClient.random()
            }
            .flatMapLeft {
                wellKnownCategories.sample().run {
                    logger.warn { "Unable to find a random track because [${it.message}], trying well known category [$this]" }
                    getCompletelyRandomTrack(this)
                }
            }

    private fun getCompletelyRandomTrack(category: String? = null) =
        run { category?.let { getRandomPlaylist(category) } ?: getRandomPlaylist() }
            .flatMap { playlist -> spotifyPlaylistService.getPlaylistTracks(playlist.id).mapLeft { it.copy() } }
            .flatMap { tracks -> tracks.sample().toEither { FAILURE_NOT_FOUND } }

    private fun getRandomPlaylist() = spotifyBrowseService.getCategories()
        .narrowFlatMap { it.sample().toEither { FAILURE_NOT_FOUND } }
        .flatMap { getRandomPlaylist(it.id) }

    private fun getRandomPlaylist(categoryId: String) = spotifyBrowseService.getCategoryPlaylists(categoryId)
        .peek { wellKnownCategories.add(categoryId) }
        .narrowFlatMap { it.playlists.items.sample().toEither { FAILURE_NOT_FOUND } }

    companion object : KLogging() {
        private val FAILURE_NOT_FOUND = GenericFailure(HttpStatus.NOT_FOUND, "")

        private fun <L, R> R?.toEither(block: () -> L) = this?.let { Either.right<L, R>(this) } ?: Either.left<L, R>(block())
    }
}
