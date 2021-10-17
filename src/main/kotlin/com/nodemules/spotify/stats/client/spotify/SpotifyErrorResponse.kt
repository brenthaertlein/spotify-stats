package com.nodemules.spotify.stats.client.spotify

import com.nodemules.spotify.stats.Failure
import org.springframework.http.HttpStatus

data class SpotifyErrorResponse(val error: SpotifyError) : Failure {
    data class SpotifyError(
        val status: Int,
        val message: String
    )

    override val status: HttpStatus = HttpStatus.valueOf(error.status)
    override val message: String = error.message
}
