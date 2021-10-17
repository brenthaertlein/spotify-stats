package com.nodemules.spotify.stats.config

import com.github.benmanes.caffeine.cache.Caffeine
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CachingConfiguration(
    @Value("\${spring.caching.time-to-live:0}") private val ttl: Long
) {

    @Bean
    fun caffeine(): Caffeine<Any, Any> = Caffeine.newBuilder()
        .expireAfterWrite(ttl, TimeUnit.SECONDS)
        .evictionListener { key, _, cause -> logger.info { "Evicting $key because $cause" } }

    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>) = CaffeineCacheManager().apply { setCaffeine(caffeine) }

    companion object : KLogging()
}