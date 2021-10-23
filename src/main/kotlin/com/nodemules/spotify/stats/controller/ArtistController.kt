package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.data.ArtistExample
import com.nodemules.spotify.stats.service.ArtistOperations
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    fun getTopGenres(@RequestParam(required = false) limit: Long?, @RequestParam(required = false) sort: String? = null) =
        artistOperations.getTopGenres(limit ?: 10, sort)
}
