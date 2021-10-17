package com.nodemules.spotify.stats

import org.springframework.http.HttpStatus

interface Failure {
    val status: HttpStatus
    val message: String
    val statusCode: Int
        get() = status.value()

    fun copy() = GenericFailure(status, message)

    data class GenericFailure(override val status: HttpStatus, override val message: String) : Failure

    companion object {
        val INTERNAL_SERVER_ERROR: Failure = GenericFailure(HttpStatus.INTERNAL_SERVER_ERROR, "Shit broke")
    }
}