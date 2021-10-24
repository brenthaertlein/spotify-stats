package com.nodemules.spotify.stats.data

import javax.validation.constraints.AssertTrue

data class TrackExample(
    val artist: String? = null,
    val genres: List<String>? = null,
    val category: String? = null
) {
    @Suppress("unused")
    @AssertTrue(message = "Only one parameter [artist, genres, category] is allowed")
    fun isValid() = listOfNotNull(artist, genres, category).size <= 1
}
