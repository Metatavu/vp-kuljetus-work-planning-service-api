package fi.metatavu.vp.workplanning.functional.auth

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenTestBuilderAuthentication
import fi.metatavu.vp.workplanning.functional.impl.RoutesTestBuilderResource
import fi.metatavu.vp.test.client.infrastructure.ApiClient
import fi.metatavu.vp.workplanning.functional.TestBuilder
import fi.metatavu.vp.workplanning.functional.settings.ApiTestSettings

/**
 * Test builder authentication
 *
 * @author Jari Nykänen
 * @author Antti Leppä
 *
 * @param testBuilder test builder instance
 * @param accessTokenProvider access token provider
 */
class TestBuilderAuthentication(
    private val testBuilder: TestBuilder,
    val accessTokenProvider: AccessTokenProvider
) : AccessTokenTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

    val routes = RoutesTestBuilderResource(testBuilder, accessTokenProvider, createClient(accessTokenProvider))

    override fun createClient(authProvider: AccessTokenProvider): ApiClient {
        val result = ApiClient(ApiTestSettings.apiBasePath)
        ApiClient.accessToken = authProvider.accessToken
        return result
    }

}