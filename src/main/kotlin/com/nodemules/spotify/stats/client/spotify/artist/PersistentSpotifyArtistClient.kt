package com.nodemules.spotify.stats.client.spotify.artist

import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.persistence.repository.ArtistRepository
import io.vavr.control.Either
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class PersistentSpotifyArtistClient(
    private val spotifyArtistFeignClient: SpotifyArtistFeignClient,
    private val artistRepository: ArtistRepository
) : SpotifyArtistClient {
    override fun getArtist(id: String): Either<SpotifyErrorResponse, Artist> = spotifyArtistFeignClient.getArtist(id)
        .map { artistRepository.save(it) }

    override fun getArtists(ids: Collection<String>): Either<SpotifyErrorResponse, List<Artist>> =
        spotifyArtistFeignClient.getArtists(ids.distinct().take(50))
            .map { artistRepository.saveAll(it.artists) }

    companion object : KLogging()
}