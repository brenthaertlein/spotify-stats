package com.nodemules.spotify.stats.persistence.repository

import com.nodemules.spotify.stats.client.spotify.Artist
import org.springframework.data.mongodb.repository.MongoRepository

interface ArtistRepository : MongoRepository<Artist, String> {
}