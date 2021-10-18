package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.service.ArtistOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/artist")
class ArtistController(
    private val artistOperations: ArtistOperations
) {

    @GetMapping("/info/genres")
    fun getGenres() = artistOperations.getGenres()
}