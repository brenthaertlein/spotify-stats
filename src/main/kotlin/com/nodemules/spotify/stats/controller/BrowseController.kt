package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.Failure.GenericFailure
import com.nodemules.spotify.stats.client.spotify.browse.Category
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
    fun getCategoriesInfo(@RequestParam(defaultValue = "id") property: String): Either<out Failure, List<String>> =
        spotifyBrowseService.getCategories()
            .map { it.foo(property) }
            .filter { it.isNotEmpty() }
            .flatMap { it.toOption() }
            .toEither { GenericFailure(HttpStatus.NOT_FOUND, "") }

    @GetMapping("/categories/{id}/playlists")
    fun getCategoryPlaylists(@PathVariable id: String) = spotifyBrowseService.getCategoryPlaylists(id)

    companion object {
        private fun Collection<Category>.foo(property: String): List<String> =
            when (property) {
                "id" -> map { it.id }
                "href" -> map { it.href }
                "name" -> map { it.name }
                else -> listOf()
            }
    }
}