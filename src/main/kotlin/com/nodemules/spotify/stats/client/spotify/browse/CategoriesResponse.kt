package com.nodemules.spotify.stats.client.spotify.browse

import com.nodemules.spotify.stats.client.spotify.PageableResponse

data class CategoriesResponse(
    val categories: PageableResponse<Category>
)
