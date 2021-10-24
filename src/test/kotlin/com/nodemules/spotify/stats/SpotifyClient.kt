package com.nodemules.spotify.stats

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.nodemules.spotify.stats.client.spotify.PageableResponse
import com.nodemules.spotify.stats.client.spotify.artist.TopTracksResponse
import com.nodemules.spotify.stats.client.spotify.browse.CategoriesResponse
import com.nodemules.spotify.stats.client.spotify.browse.CategoryPlaylistsResponse
import com.nodemules.spotify.stats.client.spotify.playlist.TrackItem
import mu.KLogging
import org.springframework.http.MediaType
import org.springframework.web.util.UriComponentsBuilder

class SpotifyClient(
    private val wireMockServer: WireMockServer
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

    private fun stubFor(mappingBuilder: MappingBuilder): StubMapping = mappingBuilder
        .also {
            it.build().apply {
                request.apply {
                    val uri = UriComponentsBuilder.fromPath(urlPath)
                    logger.info { "Stubbing request $method ${uri.toUriString()}" }
                }
            }
        }
        .let { wireMockServer.stubFor(it) }
        .also { wireMockServer.addStubMapping(it) }

    init {
        wireMockServer.apply {
            logger.info { "Resetting mappings for http://localhost:${port()}" }
            resetToDefaultMappings()
            if (!isRunning) start()
        }
    }

    companion object : KLogging() {
        private const val ACCESS_TOKEN = "FOO"
        private val objectMapper = jacksonObjectMapper().apply { registerModule(JavaTimeModule()) }
        private fun <T> okForJson(it: T): ResponseDefinitionBuilder =
            responseDefinition()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsBytes(it))
    }
}
