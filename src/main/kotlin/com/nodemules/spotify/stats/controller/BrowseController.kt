package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.client.spotify.SpotifyBrowseClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/browse")
class BrowseController(
    @Qualifier("cacheableSpotifyBrowseClient") private val spotifyBrowseClient: SpotifyBrowseClient
) {

    @GetMapping("/categories")
    fun getCategories() = spotifyBrowseClient.getCategories(pageable = PageRequest.of(0, 10))
}