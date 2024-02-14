package fi.metatavu.vp.workplanning.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.vp.test.client.apis.RoutesApi
import fi.metatavu.vp.test.client.infrastructure.ApiClient
import fi.metatavu.vp.test.client.infrastructure.ClientException
import fi.metatavu.vp.test.client.models.Route
import fi.metatavu.vp.workplanning.functional.TestBuilder
import fi.metatavu.vp.workplanning.functional.settings.ApiTestSettings
import org.junit.Assert
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Test builder resource for Routes API
 */
class RoutesTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<Route, ApiClient>(testBuilder, apiClient) {

    override fun clean(t: Route) {
        api.deleteRoute(t.id!!)
    }

    override fun getApi(): RoutesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return RoutesApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Creates new route with valid values
     *
     * @return created route
     */
    fun create(): Route {
        return addClosable(api.createRoute(Route(
            truckId = VehicleManagementMock.truckId1,
            driverId = UserManagementMock.driverId1,
            name = "Route 1",
            departureTime = OffsetDateTime.now().toString()
        )))
    }

    /**
     * Creates new route
     *
     * @param routeData route data
     * @return created route
     */
    fun create(routeData: Route): Route {
        return addClosable(api.createRoute(routeData))
    }

    /**
     * Finds route
     *
     * @param routeId route id
     * @return found route
     */
    fun findRoute(routeId: UUID): Route {
        return api.findRoute(routeId)
    }

    /**
     * Updates route
     *
     * @param id route id
     * @param route route to update
     * @return updated route
     */
    fun updateRoute(id: UUID, route: Route): Route {
        return api.updateRoute(id, route)
    }

    /**
     * Lists routes
     *
     * @param truckId vehicle id
     * @param driverId driver id
     * @param departureBefore departure before
     * @param departureAfter departure after
     * @param first first result
     * @param max max results
     * @return list of routes
     */
    fun listRoutes(
        truckId: UUID? = null,
        driverId: UUID? = null,
        departureBefore: String? = null,
        departureAfter: String? = null,
        first: Int? = null,
        max: Int? = null
    ): Array<Route> {
        return api.listRoutes(
            truckId = truckId,
            driverId = driverId,
            departureBefore = departureBefore,
            departureAfter = departureAfter,
            first = first,
            max = max
        )
    }

    /**
     * Deletes route
     *
     * @param routeId route id
     */
    fun deleteRoute(routeId: UUID) {
        api.deleteRoute(routeId)
        removeCloseable { closable: Any ->
            if (closable !is Route) {
                return@removeCloseable false
            }

            closable.id == routeId
        }
    }

    /**
     * Checks that route listing fails with expected status
     * 
     * @param expectedStatus expected status
     */
    fun assertListFail(expectedStatus: Int) {
        try {
            listRoutes()
            Assert.fail(String.format("Expected list to fail with status %d", expectedStatus))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
    
    /**
     * Asserts that route creation fails with expected status
     *
     * @param expectedStatus expected status
     * @param routeData route data
     */
    fun assertCreateFail(expectedStatus: Int, routeData: Route) {
        try {
            create(routeData)
            Assert.fail(String.format("Expected create to fail with status %d", expectedStatus))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that route find fails with expected status
     *
     * @param expectedStatus expected status
     * @param id id
     */
    fun assertFindRouteFail(expectedStatus: Int, id: UUID) {
        try {
            findRoute(id)
            Assert.fail(String.format("Expected find to fail with status %d", expectedStatus))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that route update fails with expected status
     *
     * @param expectedStatus expected status
     * @param id route id
     * @param route route to update
     */
    fun assertUpdateRouteFail(expectedStatus: Int, id: UUID, route: Route) {
        try {
            updateRoute(id, route)
            Assert.fail(String.format("Expected update to fail with status %d", expectedStatus))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that Route deletion fails with expected status
     *
     * @param expectedStatus expected status
     * @param id route id
     */
    fun assertDeleteRouteFail(expectedStatus: Int, id: UUID) {
        try {
            deleteRoute(id)
            Assert.fail(String.format("Expected delete to fail with status %d", expectedStatus))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}