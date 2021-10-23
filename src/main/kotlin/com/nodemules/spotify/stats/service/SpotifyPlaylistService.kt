package com.nodemules.spotify.stats.service

import com.nodemules.spotify.stats.client.StringCachingClient
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.SpotifyErrorResponse
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.artist.PersistentSpotifyArtistClient
import com.nodemules.spotify.stats.client.spotify.playlist.SpotifyPlaylistClient
import io.vavr.control.Either
import org.springframework.stereotype.Service

@Service
class SpotifyPlaylistService(
    private val cacheableSpotifyPlaylistClient: SpotifyPlaylistClient,
    private val persistentSpotifyArtistClient: PersistentSpotifyArtistClient,
    private val cacheableSpotifyTracksClient: StringCachingClient<Track>
) : SpotifyPlaylistOperations {

    override fun getPlaylistTracks(playlistId: String): Either<SpotifyErrorResponse, List<Track>> =
        cacheableSpotifyPlaylistClient.getPlaylistTracks(playlistId)
            .map { (items) -> items.mapNotNull { (_, _, _, track) -> track?.let { cacheableSpotifyTracksClient.put(it).orNull } } }
            .peek { tracks ->
                tracks.fold(mutableListOf<Artist>()) { list, track -> list.apply { track.album.artists.onEach { add(it) } } }
                    .filter { it.type == "artist" }
                    .map { it.id }
                    .run { takeUnless { it.isEmpty() } }
                    ?.let { persistentSpotifyArtistClient.getArtists(it) }
            }
}
