package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.service.ArtistOperations
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/artist")
class ArtistController(
    private val artistOperations: ArtistOperations
) {

    @GetMapping("")
    fun getArtists(example: ArtistExample) = artistOperations.getArtists(example)

    @GetMapping("/info/genres")
    fun getGenres() = artistOperations.getGenres()

    @GetMapping("/info/genres/top")
    fun getTopGenres(pageable: Pageable) = artistOperations.getTopGenres(pageable)
}
