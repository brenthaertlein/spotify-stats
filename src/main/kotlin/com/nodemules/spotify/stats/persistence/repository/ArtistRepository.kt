package com.nodemules.spotify.stats.persistence.repository

import com.nodemules.spotify.stats.client.spotify.Artist
import io.vavr.control.Option
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ArtistRepository : MongoRepository<Artist, String> {

    @Query("{ 'name': {\$regex: ?0, \$options: 'i'} }")
    fun findByName(name: String): Option<Artist>
}
