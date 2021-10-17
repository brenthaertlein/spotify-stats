package com.nodemules.spotify.stats.client.spotify

import org.springframework.http.HttpStatus

class SpotifyClientException(val httpStatus: HttpStatus, override val message: String) : Exception()