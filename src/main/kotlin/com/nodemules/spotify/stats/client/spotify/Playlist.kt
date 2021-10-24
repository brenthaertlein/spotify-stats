package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URI

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Playlist(
    val id: String,
    val collaborative: Boolean = false,
    val description: String? = null,
    val externalUrls: Map<String, URI> = mapOf(),
    val href: URI = URI("spotify:playlist:$id"),
    val images: List<Image> = listOf(),
    val name: String,
    val owner: User? = null,
    val snapshotId: String? = null,
    val tracks: Tracks? = null
) {

    data class Tracks(val href: URI, val total: Int)
}
