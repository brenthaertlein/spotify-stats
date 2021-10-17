package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.service.SpotifyTrackOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/track")
class TrackController(
    private val spotifyTrackService: SpotifyTrackOperations
) {

    @GetMapping("/recent/random")
    fun getRandomTrack(@RequestParam(required = false) category: String? = null) = spotifyTrackService.getRandomTrack(category)
}