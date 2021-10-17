package com.nodemules.spotify.stats.client.spotify

data class BrowseResponse<T>(
    val items: List<T>,
    val href: String,
    val next: String? = null,
    val previous: String? = null,
    val limit: Int,
    val offset: Int,
    val total: Int
)