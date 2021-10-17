package com.nodemules.spotify.stats.client

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.stereotype.Component

@Component
class OAuth2Provider(
    private val oAuth2AuthorizedClientManager: OAuth2AuthorizedClientManager
) {

    fun getAccessToken(client: String): OAuth2AccessToken? = OAuth2AuthorizeRequest.withClientRegistrationId(client)
        .principal(ANONYMOUS_AUTHENTICATION)
        .build()
        .let { oAuth2AuthorizedClientManager.authorize(it)?.accessToken }

    companion object {

        private val ANONYMOUS_AUTHENTICATION =
            AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
    }
}