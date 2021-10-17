package com.nodemules.spotify.stats

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableFeignClients
class SpotifyStatsApplication

fun main(args: Array<String>) {
    runApplication<SpotifyStatsApplication>(*args)
}
