package com.nodemules.spotify.stats

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.standalone.MappingsLoader
import mu.KLogging
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.cloud.openfeign.FeignClientProperties
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import org.springframework.http.MediaType
import org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Configuration
class FeignClientWireMockServerConfiguration(
    private val applicationContext: ConfigurableApplicationContext,
    private val oauth2ClientProperties: OAuth2ClientProperties,
    private val feignClientProperties: FeignClientProperties
) {

    init {
        initialize()
    }

    private fun initialize() {
        logger.info { "FeignClientConfiguration.initialize" }
        feignClientProperties.config.forEach { (client, _) ->
            logger.info { "Feign Client configuration found for $client" }
            applicationContext.configureFeignClient(client)
        }
    }

    private fun ConfigurableApplicationContext.configureFeignClient(client: String) {
        val property = "$FEIGN_CLIENT_CONFIG_PATH.$client.host"

        fun WireMockServer.registerBean(beanName: String) {
            logger.info { "Registering bean $beanName" }
            run { beanFactory as DefaultListableBeanFactory }
                .also {
                    it.registerBeanDefinition(
                        beanName,
                        BeanDefinitionBuilder
                            .genericBeanDefinition(WireMockServer::class.java) { this }
                            .beanDefinition
                    )
                }
            logger.info { "Registered bean $beanName: ${autowireCapableBeanFactory.getBean(beanName)}" }
        }

        fun WireMockServer.configure() {
            start()
            val host = URI("http://localhost:${port()}")

            logger.info { "Configuring WireMock server for $client at $host" }

            oauth2ClientProperties.provider[client]
                ?.apply {
                    logger.info { "Configuring OAuth2ClientProperties for $client" }
                    UriComponentsBuilder
                        .fromUri(host)
                        .path(URI(tokenUri).path)
                        .apply { tokenUri = build().toUriString() }
                        .apply { logger.info { "Assigned OAuth2 Feign Client $client: $tokenUri" } }
                }
                ?: logger.warn { "No OAuth2ClientProperties found for $client" }

            "$property=$host".let {
                logger.info { "Configuring Feign Client host for $client: $it" }
                addInlinedPropertiesToEnvironment(environment, it)
            }
            environment.getProperty(property).also { logger.info { "Changed Feign Client host for $client: $property=$it" } }
            this.registerBean(client + "ClientWireMockServer")

            addApplicationListener {
                if (it is ContextClosedEvent) {
                    stop()
                }
            }
        }

        environment.getProperty(property).also { logger.info { "Changing Feign Client host for $client: $property=$it" } }
        oauth2ClientProperties.registration[client]
            ?.let { OAuth2WireMockConfiguration(it).dynamicPort() }
            ?.let { WireMockServer(it) }
            ?.configure()
    }

    companion object : KLogging() {
        private const val FEIGN_CLIENT_CONFIG_PATH = "feign.client.config"
    }

    private class OAuth2WireMockConfiguration(
        private val oauth2Registration: OAuth2ClientProperties.Registration
    ) : WireMockConfiguration() {

        private val oauth2MappingBuilder = WireMock.post("/token")
            .willReturn(
                ResponseDefinitionBuilder.responseDefinition()
                    .withStatus(200)
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody("""{"access_token":"FOO","token_type":"Bearer","expires_in":3600}""")
            )

        override fun mappingsLoader() = MappingsLoader { mappings ->
            mappings.addMapping(
                oauth2Registration
                    .run { "grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret" }
                    .let { oauth2MappingBuilder.withRequestBody(WireMock.equalTo(it)).build() }
            )
        }

        companion object {
        }
    }
}
