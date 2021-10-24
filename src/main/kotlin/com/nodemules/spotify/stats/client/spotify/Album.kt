package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URI

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Album(
    val id: String,
    val albumType: String = "album",
    val artists: List<Artist> = listOf(),
    val externalUrls: Map<String, URI> = mapOf(),
    val href: URI = URI("https://api.spotify.com/v1/albums/$id"),
    val images: List<Image> = listOf(),
    val name: String,
    val type: String = "album",
    val uri: URI = URI("spotify:album:$id")
)
