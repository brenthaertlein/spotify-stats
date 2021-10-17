package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.client.spotify.Category
import io.vavr.control.Either

interface SpotifyBrowseOperations {

    fun getCategories(): Either<Failure, List<Category>>
}