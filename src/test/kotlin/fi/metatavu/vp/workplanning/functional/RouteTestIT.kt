package fi.metatavu.vp.workplanning.functional

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.invalid.InvalidValueTestScenarioBuilder
import fi.metatavu.invalid.InvalidValueTestScenarioPath
import fi.metatavu.invalid.InvalidValues
import fi.metatavu.vp.workplanning.functional.settings.ApiTestSettings
import fi.metatavu.vp.test.client.models.Route
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.Method
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

/**
 * A test class for testing Routes API
 */
@QuarkusTest
class RouteTestIT : AbstractFunctionalTest() {

    @Test
    fun testList() = createTestBuilder().use {
        val vehicleId = UUID.randomUUID()
        val vehicleId2 = UUID.randomUUID()
        val driverId = UUID.randomUUID()
        val driverId2 = UUID.randomUUID()
        it.user.routes.create(Route(vehicleId = vehicleId, driverId = driverId))
        it.user.routes.create(Route(vehicleId = vehicleId2, driverId = driverId))
        it.user.routes.create(Route(vehicleId = vehicleId2, driverId = driverId2))
        val totalList = it.user.routes.listRoutes()
        Assertions.assertEquals(3, totalList.size)

        val pagedList = it.user.routes.listRoutes(first = 1, max = 1)
        Assertions.assertEquals(1, pagedList.size)

        val byDriver = it.user.routes.listRoutes(driverId = driverId)
        Assertions.assertEquals(2, byDriver.size)

        val byVehicle = it.user.routes.listRoutes(vehicleId = vehicleId)
        Assertions.assertEquals(1, byVehicle.size)
    }

    @Test
    fun testCreate() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.user.routes.create(routeData)
        Assertions.assertNotNull(createdRoute)
        Assertions.assertNotNull(createdRoute.id)
        Assertions.assertEquals(routeData.vehicleId, createdRoute.vehicleId)
        Assertions.assertEquals(routeData.driverId, createdRoute.driverId)
    }

    @Test
    fun testFind() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.user.routes.create(routeData)
        val foundRoute = it.user.routes.findRoute(createdRoute.id!!)
        Assertions.assertNotNull(foundRoute)
        Assertions.assertEquals(createdRoute.id, foundRoute.id)
        Assertions.assertEquals(routeData.vehicleId, foundRoute.vehicleId)
        Assertions.assertEquals(routeData.driverId, foundRoute.driverId)
    }

    @Test
    fun testFindFail() = createTestBuilder().use {
        val createdRoute = it.user.routes.create(Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID()))

        InvalidValueTestScenarioBuilder(
            path = "v1/routes/{routeId}",
            method = Method.GET,
            token = it.user.accessTokenProvider.accessToken,
            basePath = ApiTestSettings.apiBasePath
        )
            .path(
                InvalidValueTestScenarioPath(
                    name = "routeId",
                    values = InvalidValues.STRING_NOT_NULL,
                    expectedStatus = 404,
                    default = createdRoute.id
                )
            )
            .build()
            .test()

    }

    @Test
    fun testUpdate() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.user.routes.create(routeData)
        val updatedRouteData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val updatedRoute = it.user.routes.updateRoute(createdRoute.id!!, updatedRouteData)
        Assertions.assertNotNull(updatedRoute)
        Assertions.assertEquals(createdRoute.id, updatedRoute.id)
        Assertions.assertEquals(updatedRouteData.vehicleId, updatedRoute.vehicleId)
        Assertions.assertEquals(updatedRouteData.driverId, updatedRoute.driverId)
    }

    @Test
    fun testUpdateFail() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.user.routes.create(routeData)

        InvalidValueTestScenarioBuilder(
            path = "v1/routes/{routeId}",
            method = Method.PUT,
            token = it.user.accessTokenProvider.accessToken,
            basePath = ApiTestSettings.apiBasePath,
            body = jacksonObjectMapper().writeValueAsString(createdRoute)
        )
            .path(
                InvalidValueTestScenarioPath(
                    name = "routeId",
                    values = InvalidValues.STRING_NOT_NULL,
                    expectedStatus = 404,
                    default = createdRoute.id
                )
            )
            .build()
            .test()
    }

    @Test
    fun testDelete() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.user.routes.create(routeData)
        it.user.routes.deleteRoute(createdRoute.id!!)
        val routes = it.user.routes.listRoutes()
        Assertions.assertEquals(0, routes.size)
    }

    @Test
    fun testDeleteFail() = createTestBuilder().use {
        val createdRoute = it.user.routes.create(Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID()))
        InvalidValueTestScenarioBuilder(
            path = "v1/routes/{routeId}",
            method = Method.DELETE,
            token = it.user.accessTokenProvider.accessToken,
            basePath = ApiTestSettings.apiBasePath
        )
            .path(
                InvalidValueTestScenarioPath(
                    name = "routeId",
                    values = InvalidValues.STRING_NOT_NULL,
                    expectedStatus = 404,
                    default = createdRoute.id
                )
            )
            .build()
            .test()
    }


}