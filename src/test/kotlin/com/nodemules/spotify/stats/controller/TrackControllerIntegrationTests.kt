package com.nodemules.spotify.stats.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.nodemules.spotify.stats.SpotifyClient
import com.nodemules.spotify.stats.categoriesPlaylistResponse
import com.nodemules.spotify.stats.categoriesResponse
import com.nodemules.spotify.stats.client.spotify.Album
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.Playlist
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.artist.TopTracksResponse
import com.nodemules.spotify.stats.client.spotify.browse.Category
import com.nodemules.spotify.stats.playlistResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrackControllerIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mongoTemplate: MongoTemplate,
    @Qualifier("spotifyClientWireMockServer") @Autowired private val wireMockServer: WireMockServer
) {
    private val spotifyClient: SpotifyClient = SpotifyClient(wireMockServer)

    @BeforeEach
    fun beforeEach() {
        mongoTemplate.remove(Query(), Artist::class.java)
        mongoTemplate.insertAll(ARTISTS)
    }

    @Test
    fun `getRandomTrack - SUCCESS`() {
        spotifyClient.getCategories { SpotifyClient.categoriesResponse(CATEGORY_PUNK) }
        spotifyClient.getPlaylists("punk") { SpotifyClient.categoriesPlaylistResponse(PLAYLIST_PUNK_SHIT) }
        spotifyClient.getPlaylistTracks("punk_shit") { SpotifyClient.playlistResponse(TRACK_WHITE_RIOT) }

        mockMvc.get("/track/recent/random")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("name") { value("White Riot") }
            }
    }

    @Test
    fun `getRandomTrack - SUCCESS - category=punk`() {
        spotifyClient.getPlaylists("punk") { SpotifyClient.categoriesPlaylistResponse(PLAYLIST_PUNK_SHIT) }
        spotifyClient.getPlaylistTracks("punk_shit") { SpotifyClient.playlistResponse(TRACK_WHITE_RIOT) }

        mockMvc.get("/track/recent/random") {
            param("category", "punk")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("name") { value("White Riot") }
            }
    }

    @Test
    fun `getRandomTracks - SUCCESS - artist=thursday`() {
        spotifyClient.getTopTracks("thursday") { TopTracksResponse(tracks = listOf(TRACK_UNDERSTANDING_IN_A_CAR_CRASH)) }

        mockMvc.get("/track/recent/random") {
            param("artist", "thursday")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("name") { value("Understanding In A Car Crash") }
            }
    }

    @Test
    fun `getRandomTracks - SUCCESS - genres=new jersey,emo`() {
        spotifyClient.getTopTracks("thursday") { TopTracksResponse(tracks = listOf(TRACK_UNDERSTANDING_IN_A_CAR_CRASH)) }

        mockMvc.get("/track/recent/random") {
            param("genres", "new jersey,emo")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("name") { value("Understanding In A Car Crash") }
            }
    }

    companion object {

        private val ARTIST_THE_CLASH = Artist(
            id = "the_clash",
            name = "The Clash",
            popularity = 98
        )

        private val ARTIST_THURSDAY = Artist(
            id = "thursday",
            name = "Thursday",
            popularity = 95,
            genres = listOf("emo", "new jersey hardcore", "pop punk", "post-hardcore", "screamo")
        )

        private val ALBUM_THE_CLASH = Album(
            id = "the_clash_1977",
            name = "The Clash",
            artists = listOf(ARTIST_THE_CLASH)
        )

        private val ALBUM_FULL_COLLAPSE = Album(
            id = "full_collapse",
            name = "Full Collapse",
            artists = listOf(ARTIST_THURSDAY)
        )

        private val TRACK_WHITE_RIOT = Track(
            id = "white_riot",
            name = "White Riot",
            album = ALBUM_THE_CLASH,
            artists = listOf(ARTIST_THE_CLASH),
            durationMs = TimeUnit.SECONDS.toMillis(116),
            discNumber = 1,
            trackNumber = 4,
            popularity = 98
        )

        private val TRACK_UNDERSTANDING_IN_A_CAR_CRASH = Track(
            id = "understanding_in_a_car_crash",
            name = "Understanding In A Car Crash",
            album = ALBUM_FULL_COLLAPSE,
            artists = listOf(ARTIST_THURSDAY),
            discNumber = 1,
            trackNumber = 2,
            durationMs = 264546,
            popularity = 57
        )

        private val CATEGORY_PUNK = Category(id = "punk", name = "Punk")

        private val PLAYLIST_PUNK_SHIT = Playlist(id = "punk_shit", name = "The most punk shit you'll ever hear")

        private val ARTISTS = listOf(ARTIST_THE_CLASH, ARTIST_THURSDAY)
    }
}
