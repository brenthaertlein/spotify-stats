package com.nodemules.spotify.stats

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.standalone.MappingsLoader
import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.artist.TopTracksResponse
import com.nodemules.spotify.stats.client.spotify.browse.CategoriesResponse
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import com.nodemules.spotify.stats.client.spotify.playlist.TrackItem
import org.springframework.http.MediaType

class SpotifyClient(
    port: Int,
    clientId: String = "client_id",
    clientSecret: String = "client_secret"
) : WireMockServer(
    OAuth2WireMockConfiguration(clientId, clientSecret).port(port)
) {

    fun getCategories(response: () -> CategoriesResponse) {
        stubFor(
            get(urlPathEqualTo("/v1/browse/categories"))
                .withHeader("Authorization", equalTo("Bearer $ACCESS_TOKEN"))
                .withQueryParam("limit", equalTo("50"))
                .willReturn(okForJson(response()))
        )
    }

    fun getPlaylists(categoryId: String, response: () -> CategoryPlaylistsResponse) {
        stubFor(
            get(urlPathEqualTo("/v1/browse/categories/$categoryId/playlists"))
                .withHeader("Authorization", equalTo("Bearer $ACCESS_TOKEN"))
                .willReturn(okForJson(response()))
        )
    }

    fun getPlaylistTracks(playlistId: String, response: () -> PageableResponse<TrackItem>) {
        stubFor(
            get(urlPathEqualTo("/v1/playlists/$playlistId/tracks"))
                .withHeader("Authorization", equalTo("Bearer $ACCESS_TOKEN"))
                .willReturn(okForJson(response()))
        )
    }

    fun getTopTracks(artistId: String, response: () -> TopTracksResponse) {
        stubFor(
            get(urlPathEqualTo("/v1/artists/$artistId/top-tracks"))
                .withHeader("Authorization", equalTo("Bearer $ACCESS_TOKEN"))
                .withQueryParam("market", equalTo("US"))
                .willReturn(okForJson(response()))
        )
    }

    init {
        start()
    }

    private class OAuth2WireMockConfiguration(
        private val clientId: String,
        private val clientSecret: String
    ) : WireMockConfiguration() {
        override fun mappingsLoader() = MappingsLoader {
            it.addMapping(
                OAUTH2_TOKEN_MAPPING
                    .withRequestBody(equalTo("grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret"))
                    .build()
            )
        }

        companion object {
            private val OAUTH2_TOKEN_MAPPING = post("/token")
                .willReturn(
                    responseDefinition()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""{"access_token":"$ACCESS_TOKEN","token_type":"Bearer","expires_in":3600}""")
                )
        }
    }

    companion object {
        private const val ACCESS_TOKEN = "FOO"
        private val objectMapper = jacksonObjectMapper().apply { registerModule(JavaTimeModule()) }
        private fun <T> okForJson(it: T): ResponseDefinitionBuilder =
            responseDefinition()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsBytes(it))
    }
}
