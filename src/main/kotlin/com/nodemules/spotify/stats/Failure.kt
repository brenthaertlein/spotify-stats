package com.nodemules.spotify.stats

import org.springframework.http.HttpStatus

interface Failure {
    val status: HttpStatus
    val message: String
    val statusCode: Int
        get() = status.value()

    fun copy() = object : Failure {
        override val status: HttpStatus = this@Failure.status
        override val message: String = this@Failure.message
    }

    companion object {
        val INTERNAL_SERVER_ERROR: Failure = object : Failure {
            override val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
            override val message: String = "Shit broke"
        }
    }
}