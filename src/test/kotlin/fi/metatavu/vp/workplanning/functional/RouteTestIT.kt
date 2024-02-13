package fi.metatavu.vp.workplanning.functional

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.invalid.InvalidValueTestScenarioBody
import fi.metatavu.invalid.InvalidValueTestScenarioBuilder
import fi.metatavu.invalid.InvalidValueTestScenarioPath
import fi.metatavu.invalid.InvalidValues
import fi.metatavu.vp.test.client.models.Route
import fi.metatavu.vp.workplanning.functional.impl.InvalidTestValues
import fi.metatavu.vp.workplanning.functional.impl.UserManagementMock
import fi.metatavu.vp.workplanning.functional.impl.VehicleManagementMock
import fi.metatavu.vp.workplanning.functional.settings.ApiTestSettings
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.Method
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.*

/**
 * A test class for testing Routes API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(VehicleManagementMock::class),
    QuarkusTestResource(UserManagementMock::class),
)
class RouteTestIT : AbstractFunctionalTest() {

    @Test
    fun testList() = createTestBuilder().use {
        val now = OffsetDateTime.now()
        it.manager.routes.create(Route(
            truckId = VehicleManagementMock.truckId1,
            driverId = UserManagementMock.driverId1,
            name = "Route 1",
            departureTime = OffsetDateTime.now().plusDays(1).toString()
        ))
        it.manager.routes.create(Route(
            truckId = VehicleManagementMock.truckId1,
            driverId = UserManagementMock.driverId2,
            name = "Route 2",
            departureTime = OffsetDateTime.now().plusHours(1).toString()
        ))
        it.manager.routes.create(Route(
            truckId = VehicleManagementMock.truckId2,
            driverId = UserManagementMock.driverId3,
            name = "Route 3",
            departureTime = OffsetDateTime.now().minusDays(1).toString()
        ))
        val totalList = it.manager.routes.listRoutes()
        assertEquals(3, totalList.size)

        val pagedList = it.manager.routes.listRoutes(first = 1, max = 1)
        assertEquals(1, pagedList.size)

        val byDriver = it.manager.routes.listRoutes(driverId = UserManagementMock.driverId3)
        assertEquals(1, byDriver.size)

        val byTruck = it.manager.routes.listRoutes(truckId = VehicleManagementMock.truckId1)
        assertEquals(2, byTruck.size)

        val byTime = it.manager.routes.listRoutes(departureAfter = now.toString())
        assertEquals(2, byTime.size)

        val byTimeBefore = it.manager.routes.listRoutes(departureBefore = now.toString())
        assertEquals(1, byTimeBefore.size)
    }
    
    @Test
    fun testListFail() = createTestBuilder().use {
        it.user.routes.assertListFail(403)
        assertNotNull(it.manager.routes.listRoutes())
        assertNotNull(it.driver.routes.listRoutes())
    }

    @Test
    fun testCreate() = createTestBuilder().use {
        val routeData = Route(
            truckId = VehicleManagementMock.truckId1,
            driverId = UserManagementMock.driverId1,
            name = "Route 1",
            departureTime = OffsetDateTime.now().plusDays(1).toString()
        )
        val createdRoute = it.manager.routes.create(routeData)
        assertNotNull(createdRoute)
        assertNotNull(createdRoute.id)
        assertNotNull(createdRoute.createdAt)
        assertEquals(routeData.truckId, createdRoute.truckId)
        assertEquals(routeData.driverId, createdRoute.driverId)
        assertEquals(routeData.name, createdRoute.name)
        assertEquals(OffsetDateTime.parse(routeData.departureTime).toEpochSecond(),
            OffsetDateTime.parse(createdRoute.departureTime).toEpochSecond())
    }

    @Test
    fun testCreateFail() = createTestBuilder().use {
        val routeData = Route(
            truckId = UUID.randomUUID(),
            driverId = UUID.randomUUID(),
            name = "Route 1",
            departureTime = OffsetDateTime.now().toString()
        )

        it.user.routes.assertCreateFail(403, routeData)
        it.driver.routes.assertCreateFail(403, routeData)
        InvalidValueTestScenarioBuilder(
            path = "v1/routes",
            method = Method.POST,
            token = it.manager.accessTokenProvider.accessToken,
            basePath = ApiTestSettings.apiBasePath
        )
            .body(
                InvalidValueTestScenarioBody(
                    values = InvalidTestValues.routeBody,
                    expectedStatus = 400
                )
            )
            .build()
            .test()
    }

    @Test
    fun testFind() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create()
        val foundRoute = it.manager.routes.findRoute(createdRoute.id!!)
        assertNotNull(foundRoute)
        assertEquals(createdRoute.id, foundRoute.id)
        assertEquals(createdRoute.truckId, foundRoute.truckId)
        assertEquals(createdRoute.driverId, foundRoute.driverId)
    }

    @Test
    fun testFindFail() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create()

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
        val createdRoute = it.manager.routes.create()
        val updatedRouteData = Route(
            truckId = VehicleManagementMock.truckId2,
            driverId = UserManagementMock.driverId2,
            name = "Updated route",
            departureTime = createdRoute.departureTime
        )
        val updatedRoute = it.manager.routes.updateRoute(createdRoute.id!!, updatedRouteData)
        assertNotNull(updatedRoute)
        assertEquals(createdRoute.id, updatedRoute.id)
        assertEquals(updatedRouteData.truckId, updatedRoute.truckId)
        assertEquals(updatedRouteData.driverId, updatedRoute.driverId)
    }

    @Test
    fun testUpdateFail() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create()

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
            .body(
                InvalidValueTestScenarioBody(
                    values = InvalidTestValues.routeBody,
                    expectedStatus = 400
                )
            )
            .build()
            .test()
    }

    @Test
    fun testDelete() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create()
        it.manager.routes.deleteRoute(createdRoute.id!!)
        val routes = it.manager.routes.listRoutes()
        assertEquals(0, routes.size)
    }

    @Test
    fun testDeleteFail() = createTestBuilder().use {
        val createdRoute = it.manager.routes.create()

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