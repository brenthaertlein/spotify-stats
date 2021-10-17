package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.service.SpotifyPlaylistOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/playlist")
class PlaylistController(
    private val spotifyPlaylistService: SpotifyPlaylistOperations
) {

    @GetMapping("/{id}/tracks")
    fun getPlaylistTracks(@PathVariable id: String) = spotifyPlaylistService.getPlaylistTracks(id)
}