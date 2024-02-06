package fi.metatavu.vp.workplanning.rest

import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*

/**
 * Abstract base class for all API services
 *
 * @author Jari Nykänen
 */
abstract class AbstractApi {

    @Inject
    private lateinit var jsonWebToken: JsonWebToken

    /**
     * Returns logged user id
     *
     * @return logged user id
     */
    protected val loggedUserId: UUID?
        get() {
            if (jsonWebToken.subject != null) {
                return UUID.fromString(jsonWebToken.subject)
            }

            return null
        }
    /**
     * Constructs ok response
     *
     * @param entity payload
     * @param count total count
     * @return response
     */
    protected fun createOk(entity: Any?, count: Long): Response {
        return Response
            .status(Response.Status.OK)
            .header("X-Total-Count", count.toString())
            .header("Access-Control-Expose-Headers", "X-Total-Count")
            .entity(entity)
            .build()
    }

    /**
     * Constructs ok response
     *
     * @param entity payload
     * @return response
     */
    protected fun createOk(entity: Any?): Response {
        return Response
            .status(Response.Status.OK)
            .entity(entity)
            .build()
    }

    /**
     * Constructs ok response
     *
     * @return response
     */
    protected fun createOk(): Response {
        return Response
            .status(Response.Status.OK)
            .build()
    }

    /**
     * Constructs no content response
     *
     * @param entity payload
     * @return response
     */
    protected fun createAccepted(entity: Any?): Response {
        return Response
            .status(Response.Status.ACCEPTED)
            .entity(entity)
            .build()
    }

    /**
     * Constructs no content response
     *
     * @return response
     */
    protected fun createNoContent(): Response {
        return Response
            .status(Response.Status.NO_CONTENT)
            .build()
    }

    /**
     * Constructs bad request response
     *
     * @param message message
     * @return response
     */
    protected fun createBadRequest(message: String): Response {
        return createError(Response.Status.BAD_REQUEST, message)
    }

    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createNotFound(message: String): Response {
        return createError(Response.Status.NOT_FOUND, message)
    }

    /**
     * Constructs not found response
     *
     * @return response
     */
    protected fun createNotFound(): Response {
        return Response
            .status(Response.Status.NOT_FOUND)
            .build()
    }
    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createConflict(message: String): Response {
        return createError(Response.Status.CONFLICT, message)
    }

    /**
     * Constructs not implemented response
     *
     * @param message message
     * @return response
     */
    protected fun createNotImplemented(message: String): Response {
        return createError(Response.Status.NOT_IMPLEMENTED, message)
    }

    /**
     * Constructs internal server error response
     *
     * @param message message
     * @return response
     */
    protected fun createInternalServerError(message: String): Response {
        return createError(Response.Status.INTERNAL_SERVER_ERROR, message)
    }

    /**
     * Constructs forbidden response
     *
     * @param message message
     * @return response
     */
    protected fun createForbidden(message: String): Response {
        return createError(Response.Status.FORBIDDEN, message)
    }

    /**
     * Constructs unauthorized response
     *
     * @param message message
     * @return response
     */
    protected fun createUnauthorized(message: String): Response {
        return createError(Response.Status.UNAUTHORIZED, message)
    }

    /**
     * Constructs an error response
     *
     * @param status status code
     * @param message message
     *
     * @return error response
     */
    private fun createError(status: Response.Status, message: String): Response {
        val entity = fi.metatavu.vp.api.model.Error(
            message = message,
            status = status.statusCode
        )

        return Response
            .status(status)
            .entity(entity)
            .build()
    }

    fun createNotFoundMessage(entity: String, id: UUID): String {
        return "$entity with id $id not found"
    }

    companion object {
        const val NOT_FOUND_MESSAGE = "Not found"
        const val UNAUTHORIZED = "Unauthorized"
        const val FORBIDDEN = "Forbidden"
        const val MISSING_REQUEST_BODY = "Missing request body"

        const val ROUTE = "Route"
    }

}
