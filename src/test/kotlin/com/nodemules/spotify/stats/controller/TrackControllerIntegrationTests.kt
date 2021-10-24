package com.nodemules.spotify.stats.controller

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.nodemules.spotify.stats.client.spotify.Album
import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.Playlist
import com.nodemules.spotify.stats.client.spotify.Track
import com.nodemules.spotify.stats.client.spotify.browse.CategoriesResponse
import com.nodemules.spotify.stats.client.spotify.browse.Category
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import com.nodemules.spotify.stats.client.spotify.playlist.TrackItem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.net.URI
import java.time.Instant
import java.util.concurrent.TimeUnit

@SpringBootTest
@AutoConfigureMockMvc
class TrackControllerIntegrationTests(
    @Autowired val mockMvc: MockMvc
) : DisposableBean {

    override fun destroy() {
        spotifyClient.stop()
    }

    @Test
    fun `getRandomTrack - SUCCEED`() {
        spotifyClient.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/v1/browse/categories"))
                .withQueryParam("limit", WireMock.equalTo("50"))
                .willReturn(okForJson(categoriesResponse(Category(id = "punk", href = "", icons = listOf(), name = "Punk"))))
        )

        spotifyClient.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/v1/browse/categories/punk/playlists"))
                .willReturn(
                    okForJson(
                        categoriesPlaylistResponse(
                            Playlist(href = URI(""), id = "punk_shit", name = "The most punk shit you'll ever hear", snapshotId = "")
                        )
                    )
                )
        )

        spotifyClient.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/v1/playlists/punk_shit/tracks"))
                .willReturn(
                    okForJson(
                        playlistResponse(
                            Track(
                                id = "white_riot",
                                name = "White Riot",
                                album = Album(
                                    id = "the_clash_1977",
                                    name = "The Clash"
                                ),
                                durationMs = TimeUnit.SECONDS.toMillis(116),
                                discNumber = 1,
                                trackNumber = 4,
                                popularity = 98
                            )
                        )
                    )
                )
        )

        mockMvc.get("/track/recent/random")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("name") { value("White Riot") }
            }
    }

    companion object {
        private val objectMapper = jacksonObjectMapper().apply { registerModule(JavaTimeModule()) }

        private val spotifyClient = WireMockServer(12345).apply {
            start()
            stubFor(
                WireMock.post("/token")
                    .willReturn(
                        ResponseDefinitionBuilder.responseDefinition()
                            .withStatus(200)
                            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                            .withBody(
                                """
                                {
                                    "access_token"      : "foo",
                                    "token_type"        : "Bearer",
                                    "expires_in"        : 3600
                                }
                                """.trimIndent()
                            )
                    )
            )
        }

        fun <T> okForJson(it: T): ResponseDefinitionBuilder =
            ResponseDefinitionBuilder.responseDefinition()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsBytes(it))

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
