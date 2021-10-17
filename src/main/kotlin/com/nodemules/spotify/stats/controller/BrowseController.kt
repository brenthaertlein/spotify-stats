package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.service.SpotifyBrowseOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/browse")
class BrowseController(
    private val spotifyBrowseService: SpotifyBrowseOperations
) {

    @GetMapping("/categories")
    fun getCategories() = spotifyBrowseService.getCategories()

    @GetMapping("/categories/{id}/playlists")
    fun getCategoryPlaylists(@PathVariable id: String) = spotifyBrowseService.getCategoryPlaylists(id)
}