package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.client.spotify.SpotifyBrowseFeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/browse")
class BrowseController(
    private val spotifyBrowseFeignClient: SpotifyBrowseFeignClient
) {

    @GetMapping("/categories")
    fun getCategories() = spotifyBrowseFeignClient.getCategories()
}