package com.nodemules.spotify.stats.client.spotify

data class Category(
    val href: String,
    val icons: List<Image>,
    val id: String,
    val name: String
) {
}