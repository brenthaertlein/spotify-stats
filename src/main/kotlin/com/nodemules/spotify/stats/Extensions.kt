package com.nodemules.spotify.stats

fun <T> Collection<T>.sample() = this.asSequence().shuffled().find { true }