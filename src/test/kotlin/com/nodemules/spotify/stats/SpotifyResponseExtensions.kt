package com.nodemules.spotify.stats

import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.Playlist
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.browse.CategoriesResponse
import com.nodemules.spotify.stats.client.spotify.browse.Category
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import com.nodemules.spotify.stats.client.spotify.playlist.TrackItem
import java.time.Instant

fun SpotifyClient.Companion.playlistResponse(vararg tracks: Track): PageableResponse<TrackItem> = PageableResponse(
    items = tracks.map { TrackItem(track = it, addedAt = Instant.now()) },
    href = "",
    limit = 20,
    offset = 0,
    total = tracks.size
)

fun SpotifyClient.Companion.categoriesPlaylistResponse(vararg playlists: Playlist) =
    CategoryPlaylistsResponse(
        playlists = PageableResponse(
            items = listOf(*playlists),
            href = "",
            limit = 20,
            offset = 0,
            total = playlists.size
        )
    )

fun SpotifyClient.Companion.categoriesResponse(vararg categories: Category) =
    CategoriesResponse(
        categories = PageableResponse(
            items = listOf(*categories),
            href = "",
            limit = 50,
            offset = 0,
            total = categories.size
        )
    )
