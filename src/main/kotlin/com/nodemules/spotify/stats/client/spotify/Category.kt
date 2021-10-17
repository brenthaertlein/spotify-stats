package com.nodemules.spotify.stats.client.spotify

data class Category(
    val href: String,
    val icons: List<Icon>,
    val id: String,
    val name: String
) {
}