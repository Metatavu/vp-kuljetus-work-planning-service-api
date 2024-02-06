package fi.metatavu.vp.routes

import fi.metatavu.vp.api.model.Route
import fi.metatavu.vp.api.spec.RoutesApi
import fi.metatavu.vp.rest.AbstractApi
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.asUni
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
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

    override fun listRoutes(vehicleId: UUID?, driverId: UUID?, first: Int?, max: Int?): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val ( routes, len ) = routeController.listRoutes(vehicleId, driverId, first, max)
            createOk(routes.map { routeTranslator.translate(it) }, len)
        }.asUni()

    @WithTransaction
    override fun createRoute(route: Route): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val userId = loggedUserId ?: return@async createUnauthorized(UNAUTHORIZED)
            val createdRoute = routeController.createRoute(route, userId)
            createOk(routeTranslator.translate(createdRoute))
        }.asUni()

    override fun findRoute(routeId: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val route = routeController.findRoute(routeId) ?: return@async createNotFound(createNotFoundMessage(ROUTE, routeId))
            createOk(routeTranslator.translate(route))
        }.asUni()

    @WithTransaction
    override fun updateRoute(routeId: UUID, route: Route): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            val userId = loggedUserId ?: return@async createUnauthorized(UNAUTHORIZED)
            val existingRoute = routeController.findRoute(routeId) ?: return@async createNotFound(createNotFoundMessage(ROUTE, routeId))
            val updatedRoute = routeController.updateRoute(existingRoute, route, userId)
            createOk(routeTranslator.translate(updatedRoute))
        }.asUni()

    @WithTransaction
    override fun deleteRoute(routeId: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            loggedUserId ?: return@async createUnauthorized(UNAUTHORIZED)
            val existingRoute = routeController.findRoute(routeId) ?: return@async createNotFound(createNotFoundMessage(ROUTE, routeId))
            routeController.deleteRoute(existingRoute)
            createNoContent()
        }.asUni()
}