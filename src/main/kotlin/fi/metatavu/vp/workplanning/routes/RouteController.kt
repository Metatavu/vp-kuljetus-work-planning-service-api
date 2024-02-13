package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.usermanagement.spec.DriversApi
import fi.metatavu.vp.vehiclemanagement.spec.TrucksApi
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.OffsetDateTime
import java.util.*

/**
 * Controller for routes
 */
@ApplicationScoped
class RouteController {

    @Inject
    lateinit var routeRepository: RouteRepository

    @RestClient
    lateinit var trucksApi: TrucksApi

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

        if (route.truckId != null && !truckExists(route.truckId)) {
            return false
        }

        return true
    }

    /**
     * Checks if truck exists
     *
     * @param id truck id
     * @return true if truck exists, false otherwise
     */
    suspend fun truckExists(id: UUID): Boolean {
        return try {
            trucksApi.findTruck(id).awaitSuspending().status == 200
        } catch (e: Exception) {
            logger.error("Error while searching for vehicle $id", e)
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
     * @param truckId truck id
     * @param driverId driver id
     * @param departureAfter departure after
     * @param departureBefore departure before
     * @param first first result
     * @param max max results
     * @return pair of list of routes and total count
     */
    suspend fun listRoutes(
        truckId: UUID?,
        driverId: UUID?,
        departureAfter: OffsetDateTime?,
        departureBefore: OffsetDateTime?,
        first: Int?,
        max: Int?
    ): Pair<List<Route>, Long> {
        return routeRepository.listRoutes(
            truckId = truckId,
            driverId = driverId,
            departureAfter = departureAfter,
            departureBefore = departureBefore,
            first = first,
            max = max
        )
    }

    /**
     * Creates new route
     *
     * @param routeRestData route data
     * @param userId user id
     * @return created route
     */
    suspend fun createRoute(routeRestData: fi.metatavu.vp.api.model.Route, userId: UUID): Route {
        return routeRepository.create(
            id = UUID.randomUUID(),
            name = routeRestData.name,
            departureTime = routeRestData.departureTime,
            truckId = routeRestData.truckId,
            driverId = routeRestData.driverId,
            creatorId = userId
        )
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
        route.name = routeRestData.name
        route.departureTime = routeRestData.departureTime
        route.truckId = routeRestData.truckId
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
