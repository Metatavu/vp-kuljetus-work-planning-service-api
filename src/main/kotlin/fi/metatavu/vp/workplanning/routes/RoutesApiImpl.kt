package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.api.model.Route
import fi.metatavu.vp.api.spec.RoutesApi
import fi.metatavu.vp.workplanning.rest.AbstractApi
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import java.time.OffsetDateTime
import java.util.*

@RequestScoped
@WithSession
@Suppress("unused")
class RoutesApiImpl : RoutesApi, AbstractApi() {

    @Inject
    lateinit var routeController: RouteController

    @Inject
    lateinit var routeTranslator: RouteTranslator

    @RolesAllowed(DRIVER_ROLE, MANAGER_ROLE)
    override fun listRoutes(
        truckId: UUID?,
        driverId: UUID?,
        departureAfter: OffsetDateTime?,
        departureBefore: OffsetDateTime?,
        first: Int?,
        max: Int?
    ): Uni<Response> = withCoroutineScope {
        val ( routes, len ) = routeController.listRoutes(
            truckId = truckId,
            driverId = driverId,
            departureAfter = departureAfter,
            departureBefore = departureBefore,
            first = first,
            max = max
        )
        createOk(routes.map { routeTranslator.translate(it) }, len)
    }

    @RolesAllowed(MANAGER_ROLE)
    @WithTransaction
    override fun createRoute(route: Route): Uni<Response> = withCoroutineScope {
        val userId = loggedUserId ?: return@withCoroutineScope createUnauthorized(UNAUTHORIZED)
        val isValidRoute = routeController.isValidRoute(route)
        if (!isValidRoute) {
            return@withCoroutineScope createBadRequest(INVALID_ROUTE)
        }
        val createdRoute = routeController.createRoute(route, userId)
        createOk(routeTranslator.translate(createdRoute))
    }

    @RolesAllowed(DRIVER_ROLE, MANAGER_ROLE)
    override fun findRoute(routeId: UUID): Uni<Response> = withCoroutineScope {
        val route = routeController.findRoute(routeId) ?: return@withCoroutineScope createNotFound(createNotFoundMessage(ROUTE, routeId))
        createOk(routeTranslator.translate(route))
    }

    @RolesAllowed(DRIVER_ROLE, MANAGER_ROLE)
    @WithTransaction
    override fun updateRoute(routeId: UUID, route: Route): Uni<Response> = withCoroutineScope {
        val userId = loggedUserId ?: return@withCoroutineScope createUnauthorized(UNAUTHORIZED)
        val existingRoute = routeController.findRoute(routeId) ?: return@withCoroutineScope createNotFound(createNotFoundMessage(ROUTE, routeId))
        if (route.driverId != existingRoute.driverId || route.truckId != existingRoute.truckId) {
            val isValidRoute = routeController.isValidRoute(route)
            if (!isValidRoute) {
                return@withCoroutineScope createBadRequest(INVALID_ROUTE)
            }
        }
        val updatedRoute = routeController.updateRoute(existingRoute, route, userId)
        createOk(routeTranslator.translate(updatedRoute))
    }

    @RolesAllowed(MANAGER_ROLE)
    @WithTransaction
    override fun deleteRoute(routeId: UUID): Uni<Response> = withCoroutineScope {
        loggedUserId ?: return@withCoroutineScope createUnauthorized(UNAUTHORIZED)
        val existingRoute = routeController.findRoute(routeId) ?: return@withCoroutineScope createNotFound(createNotFoundMessage(ROUTE, routeId))
        routeController.deleteRoute(existingRoute)
        createNoContent()
    }

}