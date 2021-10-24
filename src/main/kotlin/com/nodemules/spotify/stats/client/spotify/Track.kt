package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URI

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Track(
    val album: Album,
    val artists: List<Artist> = listOf(),
    val discNumber: Int,
    val durationMs: Long,
    val explicit: Boolean = false,
    val externalIds: Map<String, String> = mapOf(),
    val externalUrls: Map<String, URI> = mapOf(),
    val id: String,
    val href: URI = URI("https://api.spotify.com/v1/tracks/$id"),
    val name: String,
    val popularity: Int,
    val previewUrl: URI? = null,
    val trackNumber: Int,
    val type: String = "track",
    val uri: URI = URI("spotify:track:$id")
)
