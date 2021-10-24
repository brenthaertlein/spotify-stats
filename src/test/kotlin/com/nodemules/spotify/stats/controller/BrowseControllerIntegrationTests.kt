package com.nodemules.spotify.stats.controller

import com.github.tomakehurst.wiremock.WireMockServer
import com.nodemules.spotify.stats.SpotifyClient
import com.nodemules.spotify.stats.categoriesPlaylistResponse
import com.nodemules.spotify.stats.categoriesResponse
import com.nodemules.spotify.stats.client.spotify.Playlist
import com.nodemules.spotify.stats.client.spotify.browse.Category
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BrowseControllerIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Qualifier("spotifyClientWireMockServer") @Autowired private val wireMockServer: WireMockServer
) {
    private val spotifyClient: SpotifyClient = SpotifyClient(wireMockServer)

    @Test
    fun `getCategories - SUCCESS`() {
        spotifyClient.getCategories { SpotifyClient.categoriesResponse(*CATEGORIES) }

        mockMvc.get("/browse/categories")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("*") { value(hasSize<Category>(4)) }
                jsonPath("[*].id") { value(containsInAnyOrder("punk", "pop", "rock", "rnb")) }
            }
    }

    @Test
    fun `getCategoriesInfo - SUCCESS`() {
        spotifyClient.getCategories { SpotifyClient.categoriesResponse(*CATEGORIES) }

        mockMvc.get("/browse/categories/info/flattened")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("*") { value(containsInAnyOrder("punk", "pop", "rock", "rnb")) }
            }
    }

    @Test
    fun `getCategoriesInfo - SUCCESS - property=id`() {
        spotifyClient.getCategories { SpotifyClient.categoriesResponse(*CATEGORIES) }

        mockMvc.get("/browse/categories/info/flattened") {
            param("property", "id")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("*") { value(containsInAnyOrder("punk", "pop", "rock", "rnb")) }
            }
    }

    @Test
    fun `getCategoriesInfo - SUCCESS - property=name`() {
        spotifyClient.getCategories { SpotifyClient.categoriesResponse(*CATEGORIES) }

        mockMvc.get("/browse/categories/info/flattened") {
            param("property", "name")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("*") { value(containsInAnyOrder("Punk", "Pop", "Rock", "R&B")) }
            }
    }

    @Test
    fun `getCategoriesInfo - SUCCESS - property=href`() {
        spotifyClient.getCategories { SpotifyClient.categoriesResponse(*CATEGORIES) }

        mockMvc.get("/browse/categories/info/flattened") {
            param("property", "href")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("*") {
                    value(
                        listOf("punk", "pop", "rock", "rnb")
                            .map { "https://api.spotify.com/v1/browse/categories/$it" }
                            .toTypedArray()
                            .let { containsInAnyOrder(*it) }
                    )
                }
            }
    }

    @Test
    fun `getCategoryPlaylists - SUCCESS - punk`() {
        spotifyClient.getCategoryPlaylists("punk") { SpotifyClient.categoriesPlaylistResponse(PLAYLIST_PUNK_SHIT) }

        mockMvc.get("/browse/categories/punk/playlists")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("playlists.items[*].id") { value("punk_shit") }
            }
    }

    companion object {
        private val CATEGORY_PUNK = Category(id = "punk", name = "Punk")
        private val CATEGORY_POP = Category(id = "pop", name = "Pop")
        private val CATEGORY_ROCK = Category(id = "rock", name = "Rock")
        private val CATEGORY_RNB = Category(id = "rnb", name = "R&B")

        private val PLAYLIST_PUNK_SHIT = Playlist(id = "punk_shit", name = "The most punk shit you'll ever hear")

        private val CATEGORIES = setOf(CATEGORY_PUNK, CATEGORY_POP, CATEGORY_ROCK, CATEGORY_RNB).toTypedArray()

    }
}
