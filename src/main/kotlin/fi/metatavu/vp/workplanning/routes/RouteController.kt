package fi.metatavu.vp.workplanning.routes

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.util.*

/**
 * Controller for routes
 */
@ApplicationScoped
class RouteController {

    @Inject
    lateinit var routeRepository: RouteRepository

    /**
     * Lists routes
     *
     * @param vehicleId vehicle id
     * @param driverId driver id
     * @param first first result
     * @param max max results
     * @return list of routes and list length
     */
    suspend fun listRoutes(vehicleId: UUID?, driverId: UUID?, first: Int?, max: Int?): Pair<List<Route>, Long> {
        return routeRepository.listRoutes(vehicleId, driverId, first, max)
    }

    /**
     * Creates new route
     *
     * @param routeRestData route data
     * @param userId user id
     * @return created route
     */
    suspend fun createRoute(routeRestData: fi.metatavu.vp.api.model.Route, userId: UUID): Route {
        return routeRepository.create(UUID.randomUUID(), routeRestData.vehicleId, routeRestData.driverId, userId)
    }

    /**
     * Finds route by id
     *
     * @param routeId route id
     * @return found route or null if not found
     */
    suspend fun findRoute(routeId: UUID): Route? {
        return routeRepository.findByIdSuspending(routeId)
    }

    /**
     * Updates route
     *
     * @param route route to update
     * @param routeRestData route data
     * @param userId user id
     * @return updated route
     */
    suspend fun updateRoute(route: Route, routeRestData: fi.metatavu.vp.api.model.Route, userId: UUID): Route {
        route.vehicleId = routeRestData.vehicleId
        route.driverId = routeRestData.driverId
        route.lastModifierId = userId
        return routeRepository.persistSuspending(route)
    }

    /**
     * Deletes route
     *
     * @param route route to delete
     */
    suspend fun deleteRoute(route: Route) {
        routeRepository.deleteSuspending(route)
    }

}
