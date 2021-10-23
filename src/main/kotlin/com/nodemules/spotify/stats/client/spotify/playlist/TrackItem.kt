package com.nodemules.spotify.stats.client.spotify.playlist

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.User
import java.time.Instant

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TrackItem(
    val addedAt: Instant,
    val addedBy: User?,
    val isLocal: Boolean,
    val track: Track?
)
