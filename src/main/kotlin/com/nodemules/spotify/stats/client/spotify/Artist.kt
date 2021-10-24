package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URI

@Document
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Artist(
    @Id
    val id: String,
    val name: String,
    val externalUrls: Map<String, URI> = mapOf(),
    val href: URI = URI("https://api.spotify.com/v1/artists/$id"),
    val type: String = "artist",
    val uri: URI = URI("spotify:artist:$id"),
    val genres: List<String>?,
    val images: List<Image>?,
    val followers: Followers?,
    val popularity: Int?
)
