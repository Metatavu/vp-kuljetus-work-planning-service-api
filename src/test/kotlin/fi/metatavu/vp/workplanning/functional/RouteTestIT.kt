package fi.metatavu.vp.workplanning.functional

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.invalid.InvalidValueTestScenarioBody
import fi.metatavu.invalid.InvalidValueTestScenarioBuilder
import fi.metatavu.invalid.InvalidValueTestScenarioPath
import fi.metatavu.invalid.InvalidValues
import fi.metatavu.vp.test.client.models.Route
import fi.metatavu.vp.workplanning.functional.settings.ApiTestSettings
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.Method
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
        it.manager.routes.create(Route(vehicleId = vehicleId, driverId = driverId))
        it.manager.routes.create(Route(vehicleId = vehicleId2, driverId = driverId))
        it.manager.routes.create(Route(vehicleId = vehicleId2, driverId = driverId2))
        val totalList = it.manager.routes.listRoutes()
        assertEquals(3, totalList.size)

        val pagedList = it.manager.routes.listRoutes(first = 1, max = 1)
        assertEquals(1, pagedList.size)

        val byDriver = it.manager.routes.listRoutes(driverId = driverId)
        assertEquals(2, byDriver.size)

        val byVehicle = it.manager.routes.listRoutes(vehicleId = vehicleId)
        assertEquals(1, byVehicle.size)
    }
    
    @Test
    fun testListFail() = createTestBuilder().use {
        it.user.routes.assertListFail(403)
        assertNotNull(it.manager.routes.listRoutes())
        assertNotNull(it.driver.routes.listRoutes())
    }

    @Test
    fun testCreate() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.manager.routes.create(routeData)
        assertNotNull(createdRoute)
        assertNotNull(createdRoute.id)
        assertNotNull(createdRoute.createdAt)
        assertEquals(routeData.vehicleId, createdRoute.vehicleId)
        assertEquals(routeData.driverId, createdRoute.driverId)
    }

    @Test
    fun testCreateFail() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())

        it.user.routes.assertCreateFail(403, routeData)
        it.driver.routes.assertCreateFail(403, routeData)
        InvalidValueTestScenarioBuilder(
            path = "v1/routes",
            method = Method.POST,
            token = it.manager.accessTokenProvider.accessToken,
            basePath = ApiTestSettings.apiBasePath
        )
            .body(
                // also add cases about invalid route data after the route verification is implemented
                InvalidValueTestScenarioBody(
                    values = InvalidValues.STRING_NOT_NULL,
                    expectedStatus = 400
                )
            )
            .build()
            .test()
    }

    @Test
    fun testFind() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.manager.routes.create(routeData)
        val foundRoute = it.manager.routes.findRoute(createdRoute.id!!)
        assertNotNull(foundRoute)
        assertEquals(createdRoute.id, foundRoute.id)
        assertEquals(routeData.vehicleId, foundRoute.vehicleId)
        assertEquals(routeData.driverId, foundRoute.driverId)
    }

    @Test
    fun testFindFail() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create(Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID()))

        it.user.routes.assertFindRouteFail(403, createdRoute.id!!)
        assertNotNull(it.driver.routes.findRoute(createdRoute.id))

        InvalidValueTestScenarioBuilder(
            path = "v1/routes/{routeId}",
            method = Method.GET,
            token = it.manager.accessTokenProvider.accessToken,
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
        val createdRoute = it.manager.routes.create(routeData)
        val updatedRouteData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val updatedRoute = it.manager.routes.updateRoute(createdRoute.id!!, updatedRouteData)
        assertNotNull(updatedRoute)
        assertEquals(createdRoute.id, updatedRoute.id)
        assertEquals(updatedRouteData.vehicleId, updatedRoute.vehicleId)
        assertEquals(updatedRouteData.driverId, updatedRoute.driverId)
    }

    @Test
    fun testUpdateFail() = createTestBuilder().use {
        val routeData = Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID())
        val createdRoute = it.manager.routes.create(routeData)

        it.user.routes.assertUpdateRouteFail(403, createdRoute.id!!, createdRoute)
        it.driver.routes.assertUpdateRouteFail(403, createdRoute.id!!, createdRoute)

        InvalidValueTestScenarioBuilder(
            path = "v1/routes/{routeId}",
            method = Method.PUT,
            token = it.manager.accessTokenProvider.accessToken,
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
        val createdRoute = it.manager.routes.create(routeData)
        it.manager.routes.deleteRoute(createdRoute.id!!)
        val routes = it.manager.routes.listRoutes()
        assertEquals(0, routes.size)
    }

    @Test
    fun testDeleteFail() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create(Route(vehicleId = UUID.randomUUID(), driverId = UUID.randomUUID()))

        it.user.routes.assertDeleteRouteFail(403, createdRoute.id!!)
        it.driver.routes.assertDeleteRouteFail(403, createdRoute.id)

        InvalidValueTestScenarioBuilder(
            path = "v1/routes/{routeId}",
            method = Method.DELETE,
            token = it.manager.accessTokenProvider.accessToken,
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