package com.nodemules.spotify.stats

import org.springframework.http.HttpStatus

data class Failure(
    val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    val message: String
)
