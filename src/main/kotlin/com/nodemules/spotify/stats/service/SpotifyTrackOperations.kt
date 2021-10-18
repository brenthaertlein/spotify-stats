package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Track
import io.vavr.control.Either

interface SpotifyTrackOperations {

    fun getRandomTrack(category: String?): Either<out Failure, Track>
}
