package com.nodemules.spotify.stats

import io.vavr.control.Either

fun <T> Collection<T>.sample() = this.asSequence().shuffled().find { true }

inline fun <L, R, R2> Either<out L, out R>.narrowFlatMap(crossinline mapper: (R) -> Either<out L, out R2>): Either<L, R2> =
    Either.narrow(this).flatMap { Either.narrow(mapper(it)) }