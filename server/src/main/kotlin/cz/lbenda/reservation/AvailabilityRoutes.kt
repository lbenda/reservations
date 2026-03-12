package cz.lbenda.reservation

import cz.lbenda.reservation.availability.AvailabilitySlotQuery
import cz.lbenda.reservation.availability.AvailabilitySlotService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
data class AvailabilitySlotResponse(
    val startAt: String,
    val endAt: String,
    val staffId: String
)

fun Route.availabilityRoutes(availabilitySlotService: AvailabilitySlotService) {
    route("/api/public/availability") {
        get {
            val businessId = call.requireQueryUuid("businessId")
            val serviceId = call.requireQueryUuid("serviceId")
            val startDate = call.requireQueryDate("startDate")
            val endDate = call.requireQueryDate("endDate")
            val timezone = call.request.queryParameters["timezone"]
                ?: throw IllegalArgumentException("Missing required query parameter timezone")
            val slotIntervalMinutes = call.request.queryParameters["slotIntervalMinutes"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Missing or invalid query parameter slotIntervalMinutes")
            val staffId = call.request.queryParameters["staffId"]?.let(UUID::fromString)

            if (endDate.isBefore(startDate)) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("endDate must not be before startDate"))
                return@get
            }

            val slots = availabilitySlotService.generateSlots(
                AvailabilitySlotQuery(
                    businessId = businessId,
                    serviceId = serviceId,
                    startDate = startDate,
                    endDate = endDate,
                    timezone = timezone,
                    slotIntervalMinutes = slotIntervalMinutes,
                    staffId = staffId
                )
            )

            call.respond(
                slots.map {
                    AvailabilitySlotResponse(
                        startAt = it.startAt.toString(),
                        endAt = it.endAt.toString(),
                        staffId = it.staffId.toString()
                    )
                }
            )
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.requireQueryUuid(name: String): UUID =
    request.queryParameters[name]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Missing or invalid query parameter $name")

private fun io.ktor.server.application.ApplicationCall.requireQueryDate(name: String): LocalDate =
    request.queryParameters[name]?.let(LocalDate::parse)
        ?: throw IllegalArgumentException("Missing or invalid query parameter $name")
