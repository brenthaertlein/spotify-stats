package com.nodemules.spotify.stats.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("application.cors")
data class CorsConfigurationProperties(
    val allowedOrigins: List<String>
)