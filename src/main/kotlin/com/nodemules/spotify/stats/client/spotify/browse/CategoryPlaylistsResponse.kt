package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.Playlist

data class CategoryPlaylistsResponse(val playlists: PageableResponse<Playlist>) {
}