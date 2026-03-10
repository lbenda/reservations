package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.Service
import cz.lbenda.reservation.catalog.ServiceCatalogService
import cz.lbenda.reservation.catalog.ServiceCommand
import cz.lbenda.reservation.catalog.ServiceValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class ServiceRequest(
    val serviceCode: String? = null,
    val name: String,
    val description: String? = null,
    val durationMinutes: Int,
    val bufferBeforeMinutes: Int? = null,
    val bufferAfterMinutes: Int? = null,
    val minAdvanceMinutes: Int? = null,
    val maxAdvanceDays: Int? = null,
    val cancellationPolicy: String? = null,
    val priceAmount: String,
    val priceCurrency: String,
    val isActive: Boolean = true
)

@Serializable
data class ServiceResponse(
    val id: String,
    val businessId: String,
    val serviceCode: String? = null,
    val name: String,
    val description: String? = null,
    val durationMinutes: Int,
    val bufferBeforeMinutes: Int? = null,
    val bufferAfterMinutes: Int? = null,
    val minAdvanceMinutes: Int? = null,
    val maxAdvanceDays: Int? = null,
    val cancellationPolicy: String? = null,
    val priceAmount: String,
    val priceCurrency: String,
    val isActive: Boolean
)

@Serializable
data class ErrorResponse(
    val message: String,
    val errors: List<FieldErrorResponse> = emptyList()
)

@Serializable
data class FieldErrorResponse(
    val field: String,
    val message: String
)

fun Route.serviceCatalogRoutes(serviceCatalogService: ServiceCatalogService) {
    route("/api/admin/services") {
        get {
            val businessId = call.requireBusinessIdHeader()
            val isActive = call.request.queryParameters["isActive"]?.toBooleanStrictOrNull()
            val services = serviceCatalogService.list(businessId, isActive).map(::toResponse)
            call.respond(services)
        }

        post {
            val businessId = call.requireBusinessIdHeader()
            val request = call.receive<ServiceRequest>()
            val created = serviceCatalogService.create(businessId, request.toCommand())
            call.respond(HttpStatusCode.Created, toResponse(created))
        }

        get("/{id}") {
            val businessId = call.requireBusinessIdHeader()
            val serviceId = call.requireServiceIdParam()
            val service = serviceCatalogService.get(businessId, serviceId)
                ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Service not found"))
            call.respond(toResponse(service))
        }

        patch("/{id}") {
            val businessId = call.requireBusinessIdHeader()
            val serviceId = call.requireServiceIdParam()
            val request = call.receive<ServiceRequest>()
            val updated = serviceCatalogService.update(businessId, serviceId, request.toCommand())
                ?: return@patch call.respond(HttpStatusCode.NotFound, ErrorResponse("Service not found"))
            call.respond(toResponse(updated))
        }

        post("/{id}/archive") {
            val businessId = call.requireBusinessIdHeader()
            val serviceId = call.requireServiceIdParam()
            val archived = serviceCatalogService.archive(businessId, serviceId)
                ?: return@post call.respond(HttpStatusCode.NotFound, ErrorResponse("Service not found"))
            call.respond(toResponse(archived))
        }
    }

    route("/api/public/services") {
        get {
            val businessId = call.request.queryParameters["businessId"]?.let(UUID::fromString)
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("Missing required query parameter businessId")
                )
            val services = serviceCatalogService.list(businessId, true).map(::toResponse)
            call.respond(services)
        }
    }
}

fun Throwable.toErrorResponse(): ErrorResponse =
    when (this) {
        is ServiceValidationException -> ErrorResponse(
            message = "Validation failed",
            errors = errors.map { FieldErrorResponse(it.field, it.message) }
        )
        is IllegalArgumentException -> ErrorResponse(message = message ?: "Bad request")
        else -> ErrorResponse(message = "Internal server error")
    }

// TODO [F-016]: Replace the temporary X-Business-Id header with business resolution
// from the authenticated admin context once roles/permissions auth wiring is implemented.
private fun io.ktor.server.application.ApplicationCall.requireBusinessIdHeader(): UUID =
    request.headers["X-Business-Id"]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Missing required header X-Business-Id")

private fun io.ktor.server.application.ApplicationCall.requireServiceIdParam(): UUID =
    parameters["id"]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Missing or invalid service id")

private fun ServiceRequest.toCommand(): ServiceCommand =
    ServiceCommand(
        serviceCode = serviceCode,
        name = name,
        description = description,
        durationMinutes = durationMinutes,
        bufferBeforeMinutes = bufferBeforeMinutes,
        bufferAfterMinutes = bufferAfterMinutes,
        minAdvanceMinutes = minAdvanceMinutes,
        maxAdvanceDays = maxAdvanceDays,
        cancellationPolicy = cancellationPolicy,
        priceAmount = BigDecimal(priceAmount),
        priceCurrency = priceCurrency,
        isActive = isActive
    )

private fun toResponse(service: Service): ServiceResponse =
    ServiceResponse(
        id = service.id.toString(),
        businessId = service.businessId.toString(),
        serviceCode = service.serviceCode,
        name = service.name,
        description = service.description,
        durationMinutes = service.durationMinutes,
        bufferBeforeMinutes = service.bufferBeforeMinutes,
        bufferAfterMinutes = service.bufferAfterMinutes,
        minAdvanceMinutes = service.minAdvanceMinutes,
        maxAdvanceDays = service.maxAdvanceDays,
        cancellationPolicy = service.cancellationPolicy,
        priceAmount = service.priceAmount.toPlainString(),
        priceCurrency = service.priceCurrency,
        isActive = service.isActive
    )
