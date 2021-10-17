package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.browse.Category
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import com.nodemules.spotify.stats.client.spotify.browse.SpotifyBrowseClient
import io.vavr.control.Either
import org.springframework.stereotype.Service

@Service
class SpotifyBrowseService(
    private val cacheableSpotifyBrowseClient: SpotifyBrowseClient
) : SpotifyBrowseOperations {
    override fun getCategories(): Either<Failure, List<Category>> = cacheableSpotifyBrowseClient.getCategories()
        .bimap({ it.copy() }) { it.categories.items }

    override fun getCategoryPlaylists(id: String): Either<Failure, CategoryPlaylistsResponse> =
        cacheableSpotifyBrowseClient.getCategoryPlaylist(id)
            .mapLeft { it.copy() }
}