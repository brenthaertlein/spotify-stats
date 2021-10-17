package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.client.StringCachingClient
import com.nodemules.spotify.stats.client.spotify.Track
import org.springframework.stereotype.Component

@Component
class SpotifyTrackService(
    private val cachingClient: StringCachingClient<Track>
) : SpotifyTrackOperations {

    override fun getRandomTrack() = cachingClient.random()
}