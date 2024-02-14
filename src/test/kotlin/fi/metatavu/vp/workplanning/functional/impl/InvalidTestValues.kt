package fi.metatavu.vp.workplanning.functional.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.invalid.InvalidValues
import fi.metatavu.invalid.providers.SimpleInvalidValueProvider
import fi.metatavu.vp.test.client.models.Route
import java.time.OffsetDateTime
import java.util.*

/**
 * Invalid test values specific for this project
 */
class InvalidTestValues: InvalidValues() {

    companion object Routes {

        // Invalid route body options
        val routeBody = listOf(
            Route(
                truckId = VehicleManagementMock.truckId1,
                driverId = UUID.randomUUID(),
                name = "Route 1",
                departureTime = OffsetDateTime.now().toString()
            ),
            Route(
                truckId = UUID.randomUUID(),
                driverId = UserManagementMock.driverId1,
                name = "Route 1",
                departureTime = OffsetDateTime.now().toString()
            )
        ).map { jacksonObjectMapper().writeValueAsString(it) }
            .map { SimpleInvalidValueProvider(it) }
    }
}