package com.nodemules.spotify.stats.data

data class Genre(
    val genre: String,
    val count: Int,
    val popularity: List<Int>
)