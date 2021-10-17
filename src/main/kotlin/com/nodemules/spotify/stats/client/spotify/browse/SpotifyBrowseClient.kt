package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import io.vavr.control.Either

interface SpotifyBrowseClient {
    fun getCategories(): Either<SpotifyErrorResponse, CategoriesResponse>
    fun getCategoryPlaylist(id: String): Either<SpotifyErrorResponse, CategoryPlaylistsResponse>
}