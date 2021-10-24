package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.data.TrackExample
import com.nodemules.spotify.stats.service.SpotifyTrackOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/track")
class TrackController(
    private val spotifyTrackService: SpotifyTrackOperations
) {

    @GetMapping("/recent/random")
    fun getRandomTrack(@Valid trackExample: TrackExample) = spotifyTrackService.getRandomTrack(trackExample)
}
