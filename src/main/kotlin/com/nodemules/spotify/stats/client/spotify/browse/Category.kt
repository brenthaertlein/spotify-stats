package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.Image
import java.net.URI

data class Category(
    val id: String,
    val href: URI = URI("https://api.spotify.com/v1/browse/categories/$id"),
    val icons: List<Image> = listOf(),
    val name: String
)
