package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Category
import com.nodemules.spotify.stats.client.spotify.CategoryPlaylistsResponse
import io.vavr.control.Either

interface SpotifyBrowseOperations {

    fun getCategories(): Either<Failure, List<Category>>
    fun getCategoryPlaylists(id: String): Either<Failure, CategoryPlaylistsResponse>?
}