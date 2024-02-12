package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.usermanagement.spec.DriversApi
import fi.metatavu.vp.vehiclemanagement.spec.VehiclesApi
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.*

/**
 * Controller for routes
 */
@ApplicationScoped
class RouteController {

    @Inject
    lateinit var routeRepository: RouteRepository

    @RestClient
    lateinit var vehiclesApi: VehiclesApi

    @RestClient
    lateinit var driversApi: DriversApi

    @Inject
    lateinit var logger: org.jboss.logging.Logger

    /**
     * Checks if route is valid
     *
     * @param route route
     * @return true if route is valid, false otherwise
     */
    suspend fun isValidRoute(route: fi.metatavu.vp.api.model.Route): Boolean {
        if (route.driverId != null && !driverExists(route.driverId)) {
            return false
        }

        if (route.vehicleId != null && !vehicleExists(route.vehicleId)) {
            return false
        }

        return true
    }

    /**
     * Checks if vehicle exists
     *
     * @param vehicleId vehicle id
     * @return true if vehicle exists, false otherwise
     */
    suspend fun vehicleExists(vehicleId: UUID): Boolean {
        return try {
            vehiclesApi.findVehicle(vehicleId).awaitSuspending().status == 200
        } catch (e: Exception) {
            logger.error("Error while searching for vehicle $vehicleId", e)
            false
        }
    }

    /**
     * Checks if driver exists
     *
     * @param driverId driver id
     * @return true if driver exists, false otherwise
     */
    suspend fun driverExists(driverId: UUID): Boolean {
        return try {
            driversApi.findDriver(driverId).awaitSuspending().status == 200
        } catch (e: Exception) {
            logger.error("Error while searching for driver $driverId", e)
            false
        }
    }

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
