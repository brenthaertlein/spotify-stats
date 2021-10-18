package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.Failure.GenericFailure
import com.nodemules.spotify.stats.service.SpotifyBrowseOperations
import io.vavr.control.Either
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/browse")
class BrowseController(
    private val spotifyBrowseService: SpotifyBrowseOperations
) {

    @GetMapping("/categories")
    fun getCategories() = spotifyBrowseService.getCategories()

    @GetMapping("/categories/info/flattened")
    fun getCategoriesInfo(@RequestParam(defaultValue = "id") property: String): Either<Failure, List<String>> =
        spotifyBrowseService.getCategories()
            .flatMap { list ->
                when (property) {
                    "id" -> Either.right(list.map { it.id })
                    "href" -> Either.right(list.map { it.href })
                    "name" -> Either.right(list.map { it.name })
                    else -> Either.left(GenericFailure(HttpStatus.BAD_REQUEST, "$property is not available on Category"))
                }
            }

    @GetMapping("/categories/{id}/playlists")
    fun getCategoryPlaylists(@PathVariable id: String) = spotifyBrowseService.getCategoryPlaylists(id)
}