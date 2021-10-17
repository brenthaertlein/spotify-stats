package com.nodemules.spotify.stats.client.spotify

data class SpotifyListQuery(
    val country: String? = null,
    val locale: String? = null,
    val limit: Int? = null,
    val offset: Int? = null
)
