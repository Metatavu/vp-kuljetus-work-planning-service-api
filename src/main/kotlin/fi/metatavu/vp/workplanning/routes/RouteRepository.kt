package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.workplanning.persistence.AbstractRepository
import io.quarkus.panache.common.Parameters
import jakarta.enterprise.context.ApplicationScoped
import java.time.OffsetDateTime
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
     * @param name route name
     * @param departureTime departure time
     * @param truckId truck id
     * @param driverId driver id
     * @param creatorId creator id
     * @return created route
     */
    suspend fun create(
        id: UUID,
        name: String,
        departureTime: OffsetDateTime,
        truckId: UUID?,
        driverId: UUID?,
        creatorId: UUID
    ): Route {
        val route = Route()
        route.id = id
        route.name = name
        route.departureTime = departureTime
        route.truckId = truckId
        route.driverId = driverId
        route.creatorId = creatorId
        route.lastModifierId = creatorId
        return persistSuspending(route)
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
        val queryBuilder = StringBuilder()
        val parameters = Parameters()

        if (truckId != null) {
            addCondition(queryBuilder, "truckId = :truckId")
            parameters.and("truckId", truckId)
        }

        if (driverId != null) {
            addCondition(queryBuilder, "driverId = :driverId")
            parameters.and("driverId", driverId)
        }

        if (departureAfter != null) {
            addCondition(queryBuilder, "departureTime >= :departureAfter")
            parameters.and("departureAfter", departureAfter)
        }

        if (departureBefore != null) {
            addCondition(queryBuilder, "departureTime <= :departureBefore")
            parameters.and("departureBefore", departureBefore)
        }

        queryBuilder.append("ORDER BY modifiedAt DESC")
        return applyFirstMaxToQuery(
            query = find(queryBuilder.toString(), parameters),
            firstIndex = first,
            maxResults = max
        )
    }


}