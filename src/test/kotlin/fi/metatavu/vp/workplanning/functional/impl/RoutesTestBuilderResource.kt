package fi.metatavu.vp.workplanning.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.vp.test.client.apis.RoutesApi
import fi.metatavu.vp.test.client.infrastructure.ApiClient
import fi.metatavu.vp.test.client.models.Route
import fi.metatavu.vp.workplanning.functional.TestBuilder
import fi.metatavu.vp.workplanning.functional.settings.ApiTestSettings
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
     * @param vehicleId vehicle id
     * @param driverId driver id
     * @param first first result
     * @param max max results
     * @return list of routes
     */
    fun listRoutes(vehicleId: UUID? = null, driverId: UUID? = null, first: Int? = null, max: Int? = null): Array<Route> {
        return api.listRoutes(vehicleId, driverId, first, max)
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
}