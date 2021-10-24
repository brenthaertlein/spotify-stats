package com.nodemules.spotify.stats.controller

import com.nodemules.spotify.stats.client.spotify.Artist
import com.nodemules.spotify.stats.client.spotify.Followers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.oneOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArtistControllerIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val mongoTemplate: MongoTemplate
) {

    @BeforeEach
    fun beforeEach() {
        mongoTemplate.remove(Query(), Artist::class.java)
        mongoTemplate.insertAll(ARTISTS)
    }

    @Test
    fun `getArtists - SUCCESS`() {
        mockMvc.get("/artist")
            .andDo { log() }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getArtists - SUCCESS - genres=new jersey,emo`() {
        mockMvc.get("/artist") {
            param("genres", "new jersey,emo")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getArtists - SUCCESS - genres=new jersey,emo name=thursday`() {
        mockMvc.get("/artist") {
            param("genres", "new jersey,emo")
            param("name", "thursday")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getArtists - SUCCESS - name=thursday`() {
        mockMvc.get("/artist") {
            param("name", "thursday")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `getGenres - SUCCESS`() {
        mockMvc.get("/artist/info/genres")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("*") { value(containsInAnyOrder("emo", "new jersey hardcore", "pop punk", "post-hardcore", "screamo")) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS`() {
        mockMvc.get("/artist/info/genres/top")
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") {
                    value(
                        containsInAnyOrder(
                            "emo",
                            "new jersey hardcore",
                            "pop punk",
                            "post-hardcore",
                            "screamo"
                        )
                    )
                }
                jsonPath("content[?(@.genre == 'emo')].count") { value(2) }
                jsonPath("content[?(@.genre == 'emo')].mostPopular.name") { value("Brand New") }
                jsonPath("content[?(@.genre == 'emo')].leastPopular.name") { value("Thursday") }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains("emo")) }
                jsonPath("content[?(@.genre == 'emo')].count") { value(contains(2)) }
                jsonPath("content[?(@.genre == 'emo')].mostPopular.name") { contains("Brand New") }
                jsonPath("content[?(@.genre == 'emo')].leastPopular.name") { contains("Thursday") }
                jsonPath("totalElements") { value(5) }
                jsonPath("totalPages") { value(5) }
                jsonPath("size") { value(1) }
                jsonPath("numberOfElements") { value(1) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1 sort=`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
            param("sort", "")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains("emo")) }
                jsonPath("content[?(@.genre == 'emo')].count") { value(contains(2)) }
                jsonPath("content[?(@.genre == 'emo')].mostPopular.name") { contains("Brand New") }
                jsonPath("content[?(@.genre == 'emo')].leastPopular.name") { contains("Thursday") }
                jsonPath("totalElements") { value(5) }
                jsonPath("totalPages") { value(5) }
                jsonPath("size") { value(1) }
                jsonPath("numberOfElements") { value(1) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1 sort=count`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
            param("sort", "count")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains(oneOf("new jersey hardcore", "pop punk", "post-hardcore"))) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1 sort=followers,asc`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
            param("sort", "followers,asc")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains(oneOf("new jersey hardcore", "pop punk", "post-hardcore"))) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1 sort=popularity,asc`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
            param("sort", "popularity,asc")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains(oneOf("new jersey hardcore", "pop punk", "post-hardcore"))) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1 sort=popularity,asc sort=followers,asc`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
            param("sort", "popularity,asc")
            param("sort", "followers,asc")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains(oneOf("new jersey hardcore", "pop punk", "post-hardcore"))) }
            }
    }

    @Test
    fun `getTopGenres - SUCCESS - size=1 sort=foo`() {
        mockMvc.get("/artist/info/genres/top") {
            param("size", "1")
            param("sort", "foo")
        }
            .andDo { log() }
            .andExpect {
                status { isOk() }
                jsonPath("content[*].genre") { value(contains(oneOf("new jersey hardcore", "pop punk", "post-hardcore"))) }
            }
    }

    companion object {
        private val ARTIST_BRAND_NEW = Artist(
            id = "brand_new",
            name = "Brand New",
            popularity = 62,
            genres = listOf("emo", "screamo"),
            followers = Followers(total = 634873)
        )

        private val ARTIST_THURSDAY = Artist(
            id = "thursday",
            name = "Thursday",
            popularity = 50,
            genres = listOf("emo", "new jersey hardcore", "pop punk", "post-hardcore", "screamo"),
            followers = Followers(total = 155167)
        )

        private val ARTISTS = listOf(ARTIST_BRAND_NEW, ARTIST_THURSDAY)
    }
}
