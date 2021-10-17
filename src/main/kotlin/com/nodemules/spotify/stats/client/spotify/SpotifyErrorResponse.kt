package com.nodemules.spotify.stats.client.spotify

data class SpotifyErrorResponse(val error: SpotifyError) {
    data class SpotifyError(
        val status: Int,
        val message: String
    )
}
