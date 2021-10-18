package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.browse.Category
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import io.vavr.control.Either

interface SpotifyBrowseOperations {

    fun getCategories(): Either<out Failure, out List<Category>>

    fun getCategoryPlaylists(id: String): Either<out Failure, CategoryPlaylistsResponse>
}