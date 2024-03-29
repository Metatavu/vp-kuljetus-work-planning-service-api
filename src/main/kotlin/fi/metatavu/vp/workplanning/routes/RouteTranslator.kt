package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.workplanning.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped

/**
 * Translates JPA routes to REST routes
 */
@ApplicationScoped
class RouteTranslator: AbstractTranslator<Route, fi.metatavu.vp.api.model.Route>() {
    override suspend fun translate(entity: Route): fi.metatavu.vp.api.model.Route {
        return fi.metatavu.vp.api.model.Route(
            id = entity.id,
            name = entity.name,
            departureTime = entity.departureTime,
            truckId = entity.truckId,
            driverId = entity.driverId,
            createdAt = entity.createdAt,
            modifiedAt = entity.modifiedAt,
            creatorId = entity.creatorId,
            lastModifierId = entity.lastModifierId
        )
    }

}
