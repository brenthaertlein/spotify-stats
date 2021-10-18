package com.nodemules.spotify.stats.client.spotify

import io.vavr.control.Either
import mu.KLogging

open class FeignClientExtensions : KLogging() {

    protected fun <T> Throwable.asFallback(block: () -> String? = { null }): Either<SpotifyErrorResponse, T> =
        Either.left<SpotifyErrorResponse, T>(this.toSpotifyErrorResponse())
            .peekLeft { logger.error(this) { block() } }

    private fun Throwable.toSpotifyErrorResponse(): SpotifyErrorResponse = SpotifyErrorResponse(this.toSpotifyError())

    private fun Throwable.toSpotifyError(): SpotifyErrorResponse.SpotifyError = when (this) {
        is SpotifyClientException -> SpotifyErrorResponse.SpotifyError(status = httpStatus.value(), message = message)
        else -> cause?.toSpotifyError() ?: SpotifyErrorResponse.SpotifyError(status = 500, message = cause?.message ?: "")
    }
}