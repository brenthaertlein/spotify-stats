package com.nodemules.spotify.stats.client.spotify.tracks

import com.github.benmanes.caffeine.cache.Cache
import com.nodemules.spotify.stats.Failure
import com.nodemules.spotify.stats.Failure.GenericFailure
import com.nodemules.spotify.stats.client.StringCachingClient
import com.nodemules.spotify.stats.client.spotify.Track
import io.vavr.control.Either
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
@CacheConfig(cacheNames = ["spotify.tracks"])
class CacheableSpotifyTracksClient(
    private val cacheManager: CacheManager
) : SpotifyTracksClient, StringCachingClient<Track> {

    @CachePut(key = "#it.id")
    override fun put(it: Track): Either<Failure, Track> = Either.right(it)

    @Cacheable(unless = "#result.isLeft")
    override fun get(id: String): Either<Failure, Track> = Either.left(Failure.INTERNAL_SERVER_ERROR)

    override fun random(): Either<Failure, Track> = cacheManager.getCache("spotify.tracks")
        ?.run { nativeCache as Cache<*, *> }
        ?.run { asMap().values.find { true } }
        ?.run { this as Either<*, *> }
        ?.run { orNull as Track }
        ?.let { Either.right(it) }
        ?: Either.left(GenericFailure(HttpStatus.NOT_FOUND, "Unable to find random track"))
}