package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.Staff
import cz.lbenda.reservation.catalog.StaffCommand
import cz.lbenda.reservation.catalog.StaffManagementService
import cz.lbenda.reservation.catalog.StaffScheduleException
import cz.lbenda.reservation.catalog.StaffScheduleExceptionCommand
import cz.lbenda.reservation.catalog.StaffScheduleRangeType
import cz.lbenda.reservation.catalog.StaffValidationException
import cz.lbenda.reservation.catalog.StaffWeeklySchedule
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleCommand
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Serializable
data class StaffRequest(
    val locationId: String,
    val displayName: String,
    val email: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val status: String
)

@Serializable
data class StaffResponse(
    val id: String,
    val businessId: String,
    val locationId: String,
    val displayName: String,
    val email: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val status: String
)

@Serializable
data class StaffWeeklyScheduleRequest(
    val dayOfWeek: Int,
    val rangeType: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class StaffWeeklyScheduleResponse(
    val id: String,
    val staffId: String,
    val dayOfWeek: Int,
    val rangeType: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class StaffScheduleExceptionRequest(
    val exceptionDate: String,
    val rangeType: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val note: String? = null
)

@Serializable
data class StaffScheduleExceptionResponse(
    val id: String,
    val staffId: String,
    val exceptionDate: String,
    val rangeType: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val note: String? = null
)

fun Route.staffRoutes(staffManagementService: StaffManagementService) {
    route("/api/admin/staff") {
        get {
            val businessId = call.requireBusinessIdHeader()
            val status = call.request.queryParameters["status"]
            call.respond(staffManagementService.list(businessId, status).map(::toResponse))
        }

        post {
            val businessId = call.requireBusinessIdHeader()
            val request = call.receive<StaffRequest>()
            val created = staffManagementService.create(businessId, request.toCommand())
            call.respond(HttpStatusCode.Created, toResponse(created))
        }

        get("/{id}") {
            val businessId = call.requireBusinessIdHeader()
            val staffId = call.requireStaffIdParam()
            val staff = staffManagementService.get(businessId, staffId)
                ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Staff not found"))
            call.respond(toResponse(staff))
        }

        patch("/{id}") {
            val businessId = call.requireBusinessIdHeader()
            val staffId = call.requireStaffIdParam()
            val request = call.receive<StaffRequest>()
            val updated = staffManagementService.update(businessId, staffId, request.toCommand())
                ?: return@patch call.respond(HttpStatusCode.NotFound, ErrorResponse("Staff not found"))
            call.respond(toResponse(updated))
        }

        route("/{id}/weekly-schedules") {
            get {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                if (staffManagementService.get(businessId, staffId) == null) {
                    return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Staff not found"))
                }
                call.respond(staffManagementService.listWeeklySchedules(businessId, staffId).map(::toResponse))
            }

            post {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                val request = call.receive<StaffWeeklyScheduleRequest>()
                val created = staffManagementService.createWeeklySchedule(businessId, staffId, request.toCommand())
                call.respond(HttpStatusCode.Created, toResponse(created))
            }

            patch("/{scheduleId}") {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                val scheduleId = call.requireScheduleIdParam()
                val request = call.receive<StaffWeeklyScheduleRequest>()
                val updated = staffManagementService.updateWeeklySchedule(businessId, staffId, scheduleId, request.toCommand())
                    ?: return@patch call.respond(HttpStatusCode.NotFound, ErrorResponse("Weekly schedule not found"))
                call.respond(toResponse(updated))
            }

            delete("/{scheduleId}") {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                val scheduleId = call.requireScheduleIdParam()
                if (!staffManagementService.deleteWeeklySchedule(businessId, staffId, scheduleId)) {
                    return@delete call.respond(HttpStatusCode.NotFound, ErrorResponse("Weekly schedule not found"))
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }

        route("/{id}/schedule-exceptions") {
            get {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                if (staffManagementService.get(businessId, staffId) == null) {
                    return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Staff not found"))
                }
                call.respond(staffManagementService.listScheduleExceptions(businessId, staffId).map(::toResponse))
            }

            post {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                val request = call.receive<StaffScheduleExceptionRequest>()
                val created = staffManagementService.createScheduleException(businessId, staffId, request.toCommand())
                call.respond(HttpStatusCode.Created, toResponse(created))
            }

            patch("/{exceptionId}") {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                val exceptionId = call.requireExceptionIdParam()
                val request = call.receive<StaffScheduleExceptionRequest>()
                val updated = staffManagementService.updateScheduleException(businessId, staffId, exceptionId, request.toCommand())
                    ?: return@patch call.respond(HttpStatusCode.NotFound, ErrorResponse("Schedule exception not found"))
                call.respond(toResponse(updated))
            }

            delete("/{exceptionId}") {
                val businessId = call.requireBusinessIdHeader()
                val staffId = call.requireStaffIdParam()
                val exceptionId = call.requireExceptionIdParam()
                if (!staffManagementService.deleteScheduleException(businessId, staffId, exceptionId)) {
                    return@delete call.respond(HttpStatusCode.NotFound, ErrorResponse("Schedule exception not found"))
                }
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun io.ktor.server.application.ApplicationCall.requireStaffIdParam(): UUID =
    parameters["id"]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Missing or invalid staff id")

private fun io.ktor.server.application.ApplicationCall.requireScheduleIdParam(): UUID =
    parameters["scheduleId"]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Missing or invalid schedule id")

private fun io.ktor.server.application.ApplicationCall.requireExceptionIdParam(): UUID =
    parameters["exceptionId"]?.let(UUID::fromString)
        ?: throw IllegalArgumentException("Missing or invalid exception id")

private fun StaffRequest.toCommand(): StaffCommand =
    StaffCommand(
        locationId = UUID.fromString(locationId),
        displayName = displayName,
        email = email,
        phone = phone,
        bio = bio,
        status = status
    )

private fun StaffWeeklyScheduleRequest.toCommand(): StaffWeeklyScheduleCommand =
    StaffWeeklyScheduleCommand(
        dayOfWeek = dayOfWeek,
        rangeType = StaffScheduleRangeType.valueOf(rangeType.uppercase()),
        startTime = LocalTime.parse(startTime),
        endTime = LocalTime.parse(endTime)
    )

private fun StaffScheduleExceptionRequest.toCommand(): StaffScheduleExceptionCommand =
    StaffScheduleExceptionCommand(
        exceptionDate = LocalDate.parse(exceptionDate),
        rangeType = StaffScheduleRangeType.valueOf(rangeType.uppercase()),
        startTime = startTime?.let(LocalTime::parse),
        endTime = endTime?.let(LocalTime::parse),
        note = note
    )

private fun toResponse(staff: Staff): StaffResponse =
    StaffResponse(
        id = staff.id.toString(),
        businessId = staff.businessId.toString(),
        locationId = staff.locationId.toString(),
        displayName = staff.displayName,
        email = staff.email,
        phone = staff.phone,
        bio = staff.bio,
        status = staff.status
    )

private fun toResponse(schedule: StaffWeeklySchedule): StaffWeeklyScheduleResponse =
    StaffWeeklyScheduleResponse(
        id = schedule.id.toString(),
        staffId = schedule.staffId.toString(),
        dayOfWeek = schedule.dayOfWeek,
        rangeType = schedule.rangeType.name,
        startTime = schedule.startTime.toString(),
        endTime = schedule.endTime.toString()
    )

private fun toResponse(exception: StaffScheduleException): StaffScheduleExceptionResponse =
    StaffScheduleExceptionResponse(
        id = exception.id.toString(),
        staffId = exception.staffId.toString(),
        exceptionDate = exception.exceptionDate.toString(),
        rangeType = exception.rangeType.name,
        startTime = exception.startTime?.toString(),
        endTime = exception.endTime?.toString(),
        note = exception.note
    )
