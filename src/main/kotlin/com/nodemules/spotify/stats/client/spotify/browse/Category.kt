package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.Image

data class Category(
    val href: String,
    val icons: List<Image>,
    val id: String,
    val name: String
) {
}