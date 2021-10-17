package com.nodemules.spotify.stats.client.spotify

import org.springframework.data.domain.Pageable

interface SpotifyBrowseClient {
    fun getCategories(pageable: Pageable): CategoriesResponse?
}