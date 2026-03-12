package cz.lbenda.reservation.availability

import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffServiceRepository
import java.time.Clock
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

data class AvailabilitySlotQuery(
    val businessId: UUID,
    val serviceId: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val timezone: String,
    val slotIntervalMinutes: Int,
    val staffId: UUID? = null
)

data class AvailabilitySlot(
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val staffId: UUID
)

interface AvailabilitySlotService {
    fun generateSlots(query: AvailabilitySlotQuery): List<AvailabilitySlot>
}

class DefaultAvailabilitySlotService(
    private val staffRepository: StaffRepository,
    private val staffServiceRepository: StaffServiceRepository,
    private val serviceRepository: ServiceRepository,
    private val staffAvailabilityScheduleService: StaffAvailabilityScheduleService,
    private val staffOccupiedTimeService: StaffOccupiedTimeService,
    private val availabilityConflictChecker: AvailabilityConflictChecker,
    private val clock: Clock = Clock.systemUTC()
) : AvailabilitySlotService {
    override fun generateSlots(query: AvailabilitySlotQuery): List<AvailabilitySlot> {
        require(!query.endDate.isBefore(query.startDate)) { "endDate must not be before startDate" }
        require(query.slotIntervalMinutes > 0) { "slotIntervalMinutes must be greater than 0" }

        val zoneId = ZoneId.of(query.timezone)
        val service = serviceRepository.findById(query.businessId, query.serviceId) ?: return emptyList()
        if (!service.isActive) return emptyList()

        val now = ZonedDateTime.now(clock).withZoneSameInstant(zoneId)
        val earliestAllowedStart = now.plusMinutes((service.minAdvanceMinutes ?: 0).toLong())
        val latestAllowedDate = service.maxAdvanceDays?.let { now.toLocalDate().plusDays(it.toLong()) }
        val candidateStaffIds = resolveCandidateStaffIds(query)

        return candidateStaffIds.flatMap { staffId ->
            staffAvailabilityScheduleService.getScheduleForRange(
                businessId = query.businessId,
                staffId = staffId,
                startDate = query.startDate,
                endDate = query.endDate
            ).flatMap { snapshot ->
                val dateStart = snapshot.date.atStartOfDay(zoneId).toOffsetDateTime()
                val dateEnd = snapshot.date.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime()
                val occupiedRanges = staffOccupiedTimeService.listOccupiedRanges(
                    businessId = query.businessId,
                    staffId = staffId,
                    windowStart = dateStart,
                    windowEnd = dateEnd
                ) + snapshot.breakRanges.map { breakRange ->
                    OccupiedTimeRange(
                        startAt = ZonedDateTime.of(snapshot.date, breakRange.startTime, zoneId).toOffsetDateTime(),
                        endAt = ZonedDateTime.of(snapshot.date, breakRange.endTime, zoneId).toOffsetDateTime(),
                        source = OccupiedTimeSource.BREAK
                    )
                }

                snapshot.workRanges.flatMap { workRange ->
                    generateSlotsForWorkRange(
                        staffId = staffId,
                        serviceDurationMinutes = service.durationMinutes,
                        bufferBeforeMinutes = service.bufferBeforeMinutes ?: 0,
                        bufferAfterMinutes = service.bufferAfterMinutes ?: 0,
                        slotIntervalMinutes = query.slotIntervalMinutes,
                        workStart = ZonedDateTime.of(snapshot.date, workRange.startTime, zoneId),
                        workEnd = ZonedDateTime.of(snapshot.date, workRange.endTime, zoneId),
                        occupiedRanges = occupiedRanges,
                        earliestAllowedStart = earliestAllowedStart,
                        latestAllowedDate = latestAllowedDate
                    )
                }
            }
        }.sortedWith(compareBy<AvailabilitySlot> { it.startAt }.thenBy { it.staffId.toString() })
    }

    private fun resolveCandidateStaffIds(query: AvailabilitySlotQuery): List<UUID> {
        if (query.staffId != null) {
            val staff = staffRepository.findById(query.businessId, query.staffId) ?: return emptyList()
            if (staff.status != "active") return emptyList()

            val isAssigned = staffServiceRepository.listByStaffId(query.staffId, isActive = true)
                .any { it.serviceId == query.serviceId }
            return if (isAssigned) listOf(query.staffId) else emptyList()
        }

        val activeStaffIds = staffRepository.listByBusiness(query.businessId, status = "active")
            .map { it.id }
            .toSet()

        return staffServiceRepository.listByServiceId(query.serviceId, isActive = true)
            .map { it.staffId }
            .filter { it in activeStaffIds }
    }

    private fun generateSlotsForWorkRange(
        staffId: UUID,
        serviceDurationMinutes: Int,
        bufferBeforeMinutes: Int,
        bufferAfterMinutes: Int,
        slotIntervalMinutes: Int,
        workStart: ZonedDateTime,
        workEnd: ZonedDateTime,
        occupiedRanges: List<OccupiedTimeRange>,
        earliestAllowedStart: ZonedDateTime,
        latestAllowedDate: LocalDate?
    ): List<AvailabilitySlot> {
        val slots = mutableListOf<AvailabilitySlot>()
        var candidateStart = workStart
        val workStartOffset = workStart.toOffsetDateTime()
        val workEndOffset = workEnd.toOffsetDateTime()

        while (!candidateStart.plusMinutes(serviceDurationMinutes.toLong()).isAfter(workEnd)) {
            val candidateEnd = candidateStart.plusMinutes(serviceDurationMinutes.toLong())
            val request = AvailabilityRequest(
                startAt = candidateStart.toOffsetDateTime(),
                endAt = candidateEnd.toOffsetDateTime(),
                bufferBeforeMinutes = bufferBeforeMinutes,
                bufferAfterMinutes = bufferAfterMinutes
            )

            if (
                !candidateStart.isBefore(earliestAllowedStart) &&
                (latestAllowedDate == null || !candidateStart.toLocalDate().isAfter(latestAllowedDate)) &&
                !request.occupiedStartAt.isBefore(workStartOffset) &&
                !request.occupiedEndAt.isAfter(workEndOffset) &&
                !availabilityConflictChecker.hasConflict(request, occupiedRanges)
            ) {
                slots += AvailabilitySlot(
                    startAt = candidateStart.toOffsetDateTime(),
                    endAt = candidateEnd.toOffsetDateTime(),
                    staffId = staffId
                )
            }

            candidateStart = candidateStart.plusMinutes(slotIntervalMinutes.toLong())
        }

        return slots
    }
}
