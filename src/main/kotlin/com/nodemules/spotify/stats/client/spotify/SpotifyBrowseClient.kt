package com.nodemules.spotify.stats.client.spotify

import io.vavr.control.Either
import org.springframework.data.domain.Pageable

interface SpotifyBrowseClient {
    fun getCategories(pageable: Pageable): Either<SpotifyErrorResponse, CategoriesResponse>
}