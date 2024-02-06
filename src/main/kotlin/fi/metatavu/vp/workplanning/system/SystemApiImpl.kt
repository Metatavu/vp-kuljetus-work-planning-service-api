package fi.metatavu.vp.workplanning.system

import fi.metatavu.vp.api.spec.SystemApi
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.Response

@RequestScoped
class SystemApiImpl: SystemApi {
    override fun ping(): Uni<Response> = Uni.createFrom().item { Response.ok("pong").build() }
}