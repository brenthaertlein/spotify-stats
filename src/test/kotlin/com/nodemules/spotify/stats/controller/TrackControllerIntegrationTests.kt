package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.SpotifyClient
import com.nodemules.spotify.stats.client.spotify.Album
import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.Playlist
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.artist.TopTracksResponse
import com.nodemules.spotify.stats.client.spotify.browse.CategoriesResponse
import com.nodemules.spotify.stats.client.spotify.browse.Category
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import com.nodemules.spotify.stats.client.spotify.playlist.TrackItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureMockMvc
class TrackControllerIntegrationTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mongoTemplate: MongoTemplate
) : DisposableBean {

    override fun destroy() {
        spotifyClient.stop()
    }

    @BeforeEach
    fun beforeEach() {
        mongoTemplate.remove(Query(), Artist::class.java)
        mongoTemplate.insertAll(ARTISTS)
    }

    @AfterEach
    fun afterEach() {
        spotifyClient.resetToDefaultMappings()
    }

    @Test
    fun `getRandomTrack - SUCCESS`() {
        spotifyClient.getCategories { categoriesResponse(CATEGORY_PUNK) }
        spotifyClient.getPlaylists("punk") { categoriesPlaylistResponse(PLAYLIST_PUNK_SHIT) }
        spotifyClient.getPlaylistTracks("punk_shit") { playlistResponse(TRACK_WHITE_RIOT) }

        mockMvc.get("/track/recent/random")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("name") { value("White Riot") }
            }
    }

    @Test
    fun `getRandomTrack - SUCCESS - category=punk`() {
        spotifyClient.getPlaylists("punk") { categoriesPlaylistResponse(PLAYLIST_PUNK_SHIT) }
        spotifyClient.getPlaylistTracks("punk_shit") { playlistResponse(TRACK_WHITE_RIOT) }

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
        private val spotifyClient = SpotifyClient(port = 12345)

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

        fun playlistResponse(vararg tracks: Track): PageableResponse<TrackItem> = PageableResponse(
            items = tracks.map { TrackItem(track = it, addedAt = Instant.now()) },
            href = "",
            limit = 20,
            offset = 0,
            total = tracks.size
        )

        fun categoriesPlaylistResponse(vararg playlists: Playlist) =
            CategoryPlaylistsResponse(
                playlists = PageableResponse(
                    items = listOf(*playlists),
                    href = "",
                    limit = 20,
                    offset = 0,
                    total = playlists.size
                )
            )

        fun categoriesResponse(vararg categories: Category) =
            CategoriesResponse(
                categories = PageableResponse(
                    items = listOf(*categories),
                    href = "",
                    limit = 50,
                    offset = 0,
                    total = categories.size
                )
            )
    }
}
