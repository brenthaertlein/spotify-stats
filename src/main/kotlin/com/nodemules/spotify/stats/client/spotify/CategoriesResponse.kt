package com.nodemules.spotify.stats.client.spotify

data class CategoriesResponse(
    val categories: PageableResponse<Category>
) {
}
