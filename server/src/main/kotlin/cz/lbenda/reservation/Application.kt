package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.ServiceCatalogService
import cz.lbenda.reservation.catalog.StaffManagementService
import cz.lbenda.reservation.availability.AvailabilitySlotService
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.respond
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module(
    serviceCatalogService: ServiceCatalogService = AppRuntime.createServiceCatalogService(),
    staffManagementService: StaffManagementService = AppRuntime.createStaffManagementService(),
    availabilitySlotService: AvailabilitySlotService = AppRuntime.createAvailabilitySlotService()
) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            ignoreUnknownKeys = true
        })
    }
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                status = io.ktor.http.HttpStatusCode.BadRequest,
                message = cause.toErrorResponse()
            )
        }
        exception<Exception> { call, cause ->
            val status = if (cause is cz.lbenda.reservation.catalog.ServiceValidationException) {
                io.ktor.http.HttpStatusCode.BadRequest
            } else if (cause is cz.lbenda.reservation.catalog.StaffValidationException) {
                io.ktor.http.HttpStatusCode.BadRequest
            } else {
                io.ktor.http.HttpStatusCode.InternalServerError
            }
            call.respond(status = status, message = cause.toErrorResponse())
        }
    }
    routing {
        get("/") {
            call.respondText("Hello from Reservation Server!")
        }
        serviceCatalogRoutes(serviceCatalogService)
        staffRoutes(staffManagementService)
        availabilityRoutes(availabilitySlotService)
    }
}
