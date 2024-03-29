package fi.metatavu.vp.workplanning.functional.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import fi.metatavu.vp.usermanagement.model.Driver
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import java.util.*

/**
 * Wiremock for user management service
 */
class UserManagementMock : QuarkusTestResourceLifecycleManager {
    private var wireMockServer: WireMockServer? = null

    private val authHeader = "Authorization"
    private val bearerPattern: StringValuePattern = WireMock.containing("Bearer")

    override fun start(): Map<String, String> {
        wireMockServer = WireMockServer(8080)
        wireMockServer!!.start()

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlPathMatching("/v1/drivers/.*"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(404)
                )
        )

        val defaultDriver = Driver(
            id = driverId1,
            displayName = "Driver full name"
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/drivers/$driverId1"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(defaultDriver)
                        )
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/drivers/$driverId2"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(defaultDriver.copy(id = driverId2))
                        )
                )
        )


        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/drivers/$driverId3"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(defaultDriver.copy(id = driverId3))
                        )
                )
        )

        return java.util.Map.of(
            "quarkus.rest-client.\"fi.metatavu.vp.usermanagement.spec.DriversApi\".url",
            wireMockServer!!.baseUrl()
        )
    }

    override fun stop() {
        if (null != wireMockServer) {
            wireMockServer!!.stop()
        }
    }

    companion object {
        val driverId1: UUID = UUID.randomUUID()
        val driverId2: UUID = UUID.randomUUID()
        val driverId3: UUID = UUID.randomUUID()
    }
}