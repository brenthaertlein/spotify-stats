package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URI

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Track(
    val album: Album,
    val artists: List<Artist>,
    val discNumber: Int,
    val durationMs: Int,
    val explicit: Boolean,
    val externalIds: Map<String, String>,
    val externalUrls: Map<String, URI>,
    val href: String,
    val id: String,
    val name: String,
    val popularity: Int,
    val previewUrl: URI?,
    val trackNumber: Int,
    val type: String,
    val uri: URI
)