package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.workplanning.AbstractRepository
import io.quarkus.panache.common.Parameters
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

/**
 * Repository for routes
 */
@ApplicationScoped
class RouteRepository : AbstractRepository<Route, UUID>() {

    /**
     * Creates a route
     *
     * @param id route id
     * @param vehicleId vehicle id
     * @param driverId driver id
     * @param creatorId creator id
     * @return created route
     */
    suspend fun create(
        id: UUID,
        vehicleId: UUID?,
        driverId: UUID?,
        creatorId: UUID
    ): Route {
        val route = Route()
        route.id = id
        route.vehicleId = vehicleId
        route.driverId = driverId
        route.creatorId = creatorId
        route.lastModifierId = creatorId
        return persistSuspending(route)
    }

    /**
     * Lists routes
     *
     * @param vehicleId vehicle id
     * @param driverId driver id
     * @param first first result
     * @param max max results
     * @return pair of list of routes and total count
     */
    suspend fun listRoutes(vehicleId: UUID?, driverId: UUID?, first: Int?, max: Int?): Pair<List<Route>, Long> {
        val queryBuilder = StringBuilder()
        val parameters = Parameters()

        if (vehicleId != null) {
            addCondition(queryBuilder, "vehicleId = :vehicleId")
            parameters.and("vehicleId", vehicleId)
        }

        if (driverId != null) {
            addCondition(queryBuilder, "driverId = :driverId")
            parameters.and("driverId", driverId)
        }

        return applyFirstMaxToQuery(
            query = find(queryBuilder.toString(), parameters),
            firstIndex = first,
            maxResults = max
        )
    }


}