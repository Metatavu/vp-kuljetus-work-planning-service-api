package fi.metatavu.vp.workplanning.functional.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import fi.metatavu.vp.vehiclemanagement.model.Vehicle
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import java.util.*

/**
 * Wiremock for vehicle management service
 */
class VehicleManagementMock : QuarkusTestResourceLifecycleManager {
    private var wireMockServer: WireMockServer? = null

    private val authHeader = "Authorization"
    private val bearerPattern: StringValuePattern = WireMock.containing("Bearer")
    override fun start(): Map<String, String> {
        wireMockServer = WireMockServer(8082)
        wireMockServer!!.start()


        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlPathMatching("/v1/vehicles/.*"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(404)
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/vehicles/${vehicleId1}"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(
                                Vehicle(
                                    id = vehicleId1,
                                    towableIds = listOf(UUID.randomUUID()),
                                    truckId = UUID.randomUUID()
                                )
                            )
                        )
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/vehicles/${vehicleId2}"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(
                                Vehicle(
                                    id = vehicleId2,
                                    towableIds = listOf(UUID.randomUUID()),
                                    truckId = UUID.randomUUID()
                                )
                            )
                        )
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/vehicles/${vehicleId3}"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(
                                Vehicle(
                                    id = vehicleId3,
                                    towableIds = listOf(UUID.randomUUID()),
                                    truckId = UUID.randomUUID()
                                )
                            )
                        )
                )
        )

        return java.util.Map.of(
            "quarkus.rest-client.\"fi.metatavu.vp.vehiclemanagement.spec.VehiclesApi\".url",
            wireMockServer!!.baseUrl()
        )
    }

    override fun stop() {
        if (null != wireMockServer) {
            wireMockServer!!.stop()
        }
    }

    companion object {
        val vehicleId1: UUID = UUID.randomUUID()
        val vehicleId2: UUID = UUID.randomUUID()
        val vehicleId3: UUID = UUID.randomUUID()
    }
}