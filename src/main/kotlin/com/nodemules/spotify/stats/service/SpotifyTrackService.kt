package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.StringCachingClient
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.sample
import io.vavr.control.Either
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class SpotifyTrackService(
    private val spotifyBrowseService: SpotifyBrowseOperations,
    private val spotifyPlaylistService: SpotifyPlaylistOperations,
    private val cachingClient: StringCachingClient<Track>
) : SpotifyTrackOperations {

    override fun getRandomTrack(category: String?): Either<Failure, Track> =
        run { category?.let { getCompletelyRandomTrack(it.lowercase()) } ?: cachingClient.random() }
            .fold({ getCompletelyRandomTrack() }) { Either.right(it) }

    private fun getCompletelyRandomTrack(category: String? = null) =
        run { category?.let { getRandomPlaylist(category) } ?: getRandomPlaylist() }
            .flatMap { playlist -> spotifyPlaylistService.getPlaylistTracks(playlist.id).mapLeft { it.copy() } }
            .flatMap { tracks -> tracks.sample().toEither { FAILURE_NOT_FOUND } }

    private fun getRandomPlaylist() = spotifyBrowseService.getCategories()
        .flatMap { it.sample().toEither { FAILURE_NOT_FOUND } }
        .flatMap { getRandomPlaylist(it.id) }

    private fun getRandomPlaylist(categoryId: String) = spotifyBrowseService.getCategoryPlaylists(categoryId)
        .flatMap { it.playlists.items.sample().toEither { FAILURE_NOT_FOUND } }

    companion object {
        private val FAILURE_NOT_FOUND = Failure.GenericFailure(HttpStatus.NOT_FOUND, "")

        private fun <L, R> R?.toEither(block: () -> L) = this?.let { Either.right<L, R>(this) } ?: Either.left(block())
    }
}