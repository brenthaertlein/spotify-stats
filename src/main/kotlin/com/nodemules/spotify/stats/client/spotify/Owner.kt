package com.nodemules.spotify.stats.client.spotify

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.net.URI

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Owner(
    val displayName: String,
    val externalUrls: Map<String, URI>,
    val href: URI,
    val id: String,
    val type: String,
    val uri: URI
)