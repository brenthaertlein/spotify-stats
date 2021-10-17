package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URI

data class CategoryPlaylistsResponse(val playlists: BrowseResponse<Playlist>) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class Playlist(
        val collaborative: Boolean,
        val description: String,
        val externalUrls: Map<String, URI>,
        val href: URI,
        val id: String,
        val images: List<Image>,
        val name: String,
        val owner: Owner,
        val snapshotId: String,
        val tracks: Tracks?
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
        data class Owner(
            val displayName: String,
            val externalUrls: Map<String, URI>,
            val href: URI,
            val id: String,
            val type: String,
            val uri: URI
        )

        data class Tracks(val href: URI, val total: Int)
    }
}