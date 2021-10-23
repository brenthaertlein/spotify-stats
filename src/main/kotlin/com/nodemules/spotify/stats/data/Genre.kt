package com.nodemules.spotify.stats.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped

data class Genre(
    val genre: String,
    val count: Int,
    val mostPopular: Artist?,
    val leastPopular: Artist?,
    val mostFollowed: Artist?,
    val leastFollowed: Artist?,
) {
    data class Artist(
        val id: String,
        val name: String,
        val uri: String,
        val popularity: Long,
        @JsonUnwrapped
        val followers: Followers
    ) {
        data class Followers(@JsonProperty("followers") val total: Long)
    }
}
