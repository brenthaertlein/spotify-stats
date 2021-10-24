package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.Failure.GenericFailure
import com.nodemules.spotify.stats.client.spotify.Album
import com.nodemules.spotify.stats.client.spotify.Artist
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
        trackExample.run {
            logger.info { "Getting a random track for $trackExample" }
            artist?.let { getTrackByArtist(it) }
                ?: genres?.let { getTrackByGenre(it) }
                ?: category?.let { getTrackByCategory(it.lowercase()) }
                ?: getTrackByCategory()
        }
            .peek { logger.info { "Found track ${it.print()} for $trackExample" } }
            .flatMapLeft {
                logger.warn { "Unable to find a random track because [${it.message}], trying cache" }
                cacheableSpotifyTracksClient.random().peek { logger.info { "Found track ${it.print()} from cache" } }
            }
            .flatMapLeft {
                wellKnownCategories.sample().run {
                    logger.warn { "Unable to find a random track because [${it.message}], trying well known category [$this]" }
                    getTrackByCategory(this)
                }
            }
            .map { it.duplicate() }

    private fun getTrackByArtist(artistName: String): Either<Failure, Track> =
        artistService.findByName(artistName)
            .narrowFlatMap { artistService.getArtistTopTracks(it.id) }
            .narrowFlatMap { it.sample().toEither { FAILURE_NOT_FOUND } }

    private fun getTrackByGenre(genres: List<String>) =
        artistService.findByGenre(genres)
            .narrowFlatMap {
                logger.info { "Found ${it.size} artists for genres $genres" }
                it.sample().toEither { FAILURE_NOT_FOUND }
            }
            .narrowFlatMap { (id, name) ->
                logger.info { "Getting top tracks for artist $name" }
                artistService.getArtistTopTracks(id)
                    .narrowFlatMap {
                        logger.info { "Found ${it.size} tracks for artist [$name]" }
                        it.sample().toEither { FAILURE_NOT_FOUND }
                    }
            }

    private fun getTrackByCategory(category: String? = null) =
        run { category?.let { getRandomPlaylist(category) } ?: getRandomPlaylist() }
            .flatMap { playlist -> spotifyPlaylistService.getPlaylistTracks(playlist.id).mapLeft { it.copy() } }
            .flatMap { tracks -> tracks.sample().toEither { FAILURE_NOT_FOUND } }
            .peek { logger.info { "Found ${it.print()} from category $category" } }

    private fun getRandomPlaylist() = spotifyBrowseService.getCategories()
        .narrowFlatMap { it.sample().toEither { FAILURE_NOT_FOUND } }
        .flatMap { getRandomPlaylist(it.id) }

    private fun getRandomPlaylist(categoryId: String) = spotifyBrowseService.getCategoryPlaylists(categoryId)
        .peek { wellKnownCategories.add(categoryId) }
        .narrowFlatMap { it.playlists.items.sample().toEither { FAILURE_NOT_FOUND } }

    private fun Track.duplicate(): Track = copy(
        album = album.copy(artists = album.artists.mapNotNull { artist -> artistService.getArtist(artist.id).getOrElseGet { artist } }),
        artists = artists.mapNotNull { artist -> artistService.getArtist(artist.id).getOrElseGet { artist } }
    )

    companion object : KLogging() {
        private val FAILURE_NOT_FOUND = GenericFailure(HttpStatus.NOT_FOUND, "")

        private fun <L, R> R?.toEither(block: () -> L) = this?.let { Either.right<L, R>(this) } ?: Either.left<L, R>(block())

        private fun Artist.print() = "Album(id=$id, name=$name)"
        private fun Album.print() = "Album(id=$id, name=$name)"
        private fun Track.print() =
            "Track(id=$id, name=$name, album=${album.print()}, artists=[${artists.joinToString(", ") { it.print() }}])"
    }
}
