package com.nodemules.spotify.stats.client

import com.nodemules.spotify.stats.Failure
import io.vavr.control.Either

interface CachingClient<ID, T> {

    fun put(it: T): Either<Failure, T>

    fun get(id: ID): Either<Failure, T>

    fun random(): Either<Failure, T>
}