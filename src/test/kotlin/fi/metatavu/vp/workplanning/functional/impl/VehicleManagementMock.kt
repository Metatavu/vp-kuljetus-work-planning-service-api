package fi.metatavu.vp.workplanning.functional.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import fi.metatavu.vp.vehiclemanagement.model.Truck
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
            WireMock.get(WireMock.urlPathMatching("/v1/trucks/.*"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(404)
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/trucks/${truckId1}"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(
                                Truck(
                                    plateNumber = "ABC-1",
                                    type = Truck.Type.TRUCK,
                                    vin = "0",
                                    activeVehicleId = UUID.randomUUID(),
                                    id = truckId1
                                )
                            )
                        )
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/trucks/${truckId2}"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(
                                Truck(
                                    plateNumber = "ABC-2",
                                    type = Truck.Type.TRUCK,
                                    vin = "1",
                                    activeVehicleId = UUID.randomUUID(),
                                    id = truckId2
                                )
                            )
                        )
                )
        )

        wireMockServer!!.stubFor(
            WireMock.get(WireMock.urlEqualTo("/v1/trucks/${truckId3}"))
                .withHeader(authHeader, bearerPattern)
                .willReturn(
                    WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            jacksonObjectMapper().writeValueAsString(
                                Truck(
                                    plateNumber = "ABC-3",
                                    type = Truck.Type.TRUCK,
                                    vin = "2",
                                    activeVehicleId = UUID.randomUUID(),
                                    id = truckId3
                                )
                            )
                        )
                )
        )

        return java.util.Map.of(
            "quarkus.rest-client.\"fi.metatavu.vp.vehiclemanagement.spec.TrucksApi\".url",
            wireMockServer!!.baseUrl()
        )
    }

    override fun stop() {
        if (null != wireMockServer) {
            wireMockServer!!.stop()
        }
    }

    companion object {
        val truckId1: UUID = UUID.fromString("32d3e7c7-a2b7-4eb6-89f5-c74a9ef3c5c5")
        val truckId2: UUID = UUID.fromString("a09a7d38-e685-4ce0-b435-4d562da27625")
        val truckId3: UUID = UUID.fromString("9ccc9ba9-b8fd-4404-88b1-e3ec90cfa681")
    }
}