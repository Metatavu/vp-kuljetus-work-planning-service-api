package fi.metatavu.vp.workplanning.functional.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.invalid.InvalidValues
import fi.metatavu.invalid.providers.SimpleInvalidValueProvider
import fi.metatavu.vp.test.client.models.Route
import java.util.*

/**
 * Invalid test values specific for this project
 */
class InvalidTestValues: InvalidValues() {

    companion object Routes {

        // Invalid route body options
        val routeBody = listOf(
            Route(
                vehicleId = VehicleManagementMock.vehicleId1,
                driverId = UUID.randomUUID()
            ),
            Route(
                vehicleId = UUID.randomUUID(),
                driverId = UserManagementMock.driverId1
            )
        ).map { jacksonObjectMapper().writeValueAsString(it) }
            .map { SimpleInvalidValueProvider(it) }
    }
}