package com.nodemules.spotify.stats.client.spotify

data class PageableQuery(
    val country: String? = null,
    val locale: String? = null,
    val limit: Int? = null,
    val offset: Int? = null
)
