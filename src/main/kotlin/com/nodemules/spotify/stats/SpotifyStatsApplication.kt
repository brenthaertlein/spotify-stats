package com.nodemules.spotify.stats

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@EnableCaching
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
class SpotifyStatsApplication

fun main(args: Array<String>) {
    runApplication<SpotifyStatsApplication>(*args)
}
