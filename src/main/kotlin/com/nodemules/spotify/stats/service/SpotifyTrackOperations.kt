package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.data.TrackExample
import io.vavr.control.Either

interface SpotifyTrackOperations {

    fun getRandomTrack(trackExample: TrackExample): Either<out Failure, Track>
}
