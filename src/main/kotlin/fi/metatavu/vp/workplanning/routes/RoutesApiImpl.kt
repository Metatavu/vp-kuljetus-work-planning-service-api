package fi.metatavu.vp.workplanning.routes

import fi.metatavu.vp.api.model.Route
import fi.metatavu.vp.api.spec.RoutesApi
import fi.metatavu.vp.workplanning.rest.AbstractApi
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.asUni
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import java.time.OffsetDateTime
import java.util.*

@RequestScoped
@WithSession
@OptIn(ExperimentalCoroutinesApi::class)
class RoutesApiImpl : RoutesApi, AbstractApi() {

    @Inject
    lateinit var routeController: RouteController

    @Inject
    lateinit var routeTranslator: RouteTranslator

    @Inject
    lateinit var vertx: Vertx

    @RolesAllowed(DRIVER_ROLE, MANAGER_ROLE)
    override fun listRoutes(
        truckId: UUID?,
        driverId: UUID?,
        departureAfter: OffsetDateTime?,
        departureBefore: OffsetDateTime?,
        first: Int?,
        max: Int?
    ): Uni<Response> = CoroutineScope(vertx.dispatcher()).async {
        val ( routes, len ) = routeController.listRoutes(
            truckId = truckId,
            driverId = driverId,
            departureAfter = departureAfter,
            departureBefore = departureBefore,
            first = first,
            max = max
        )
        createOk(routes.map { routeTranslator.translate(it) }, len)
    }.asUni()

    @RolesAllowed(MANAGER_ROLE)
    @WithTransaction
    override fun createRoute(route: Route): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val userId = loggedUserId ?: return@async createUnauthorized(UNAUTHORIZED)
            val isValidRoute = routeController.isValidRoute(route)
            if (!isValidRoute) {
                return@async createBadRequest(INVALID_ROUTE)
            }
            val createdRoute = routeController.createRoute(route, userId)
            createOk(routeTranslator.translate(createdRoute))
        }.asUni()

    @RolesAllowed(DRIVER_ROLE, MANAGER_ROLE)
    override fun findRoute(routeId: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val route = routeController.findRoute(routeId) ?: return@async createNotFound(createNotFoundMessage(ROUTE, routeId))
            createOk(routeTranslator.translate(route))
        }.asUni()

    @RolesAllowed(MANAGER_ROLE)
    @WithTransaction
    override fun updateRoute(routeId: UUID, route: Route): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val userId = loggedUserId ?: return@async createUnauthorized(UNAUTHORIZED)
            val existingRoute = routeController.findRoute(routeId) ?: return@async createNotFound(createNotFoundMessage(ROUTE, routeId))
            if (route.driverId != existingRoute.driverId || route.truckId != existingRoute.truckId) {
                val isValidRoute = routeController.isValidRoute(route)
                if (!isValidRoute) {
                    return@async createBadRequest(INVALID_ROUTE)
                }
            }
            val updatedRoute = routeController.updateRoute(existingRoute, route, userId)
            createOk(routeTranslator.translate(updatedRoute))
        }.asUni()

    @RolesAllowed(MANAGER_ROLE)
    @WithTransaction
    override fun deleteRoute(routeId: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            loggedUserId ?: return@async createUnauthorized(UNAUTHORIZED)
            val existingRoute = routeController.findRoute(routeId) ?: return@async createNotFound(createNotFoundMessage(ROUTE, routeId))
            routeController.deleteRoute(existingRoute)
            createNoContent()
        }.asUni()
}