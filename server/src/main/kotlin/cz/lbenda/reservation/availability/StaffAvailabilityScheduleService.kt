package cz.lbenda.reservation.availability

import cz.lbenda.reservation.catalog.StaffScheduleException
import cz.lbenda.reservation.catalog.StaffScheduleRangeType
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffWeeklySchedule
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AvailabilityTimeRange(
    val startTime: LocalTime,
    val endTime: LocalTime
)

enum class StaffScheduleSource {
    WEEKLY,
    EXCEPTION,
    DAY_OFF_EXCEPTION
}

data class StaffScheduleSnapshot(
    val staffId: UUID,
    val date: LocalDate,
    val source: StaffScheduleSource,
    val workRanges: List<AvailabilityTimeRange>,
    val breakRanges: List<AvailabilityTimeRange>
)

interface StaffAvailabilityScheduleService {
    fun getScheduleForDate(businessId: UUID, staffId: UUID, date: LocalDate): StaffScheduleSnapshot?
    fun getScheduleForRange(businessId: UUID, staffId: UUID, startDate: LocalDate, endDate: LocalDate): List<StaffScheduleSnapshot>
}

class DefaultStaffAvailabilityScheduleService(
    private val staffRepository: StaffRepository,
    private val staffWeeklyScheduleRepository: StaffWeeklyScheduleRepository,
    private val staffScheduleExceptionRepository: StaffScheduleExceptionRepository
) : StaffAvailabilityScheduleService {
    override fun getScheduleForDate(businessId: UUID, staffId: UUID, date: LocalDate): StaffScheduleSnapshot? {
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return null

        val exceptionsForDate = staffScheduleExceptionRepository.listByStaffId(staffId)
            .filter { it.exceptionDate == date }

        if (exceptionsForDate.isNotEmpty()) {
            return fromExceptions(staffId, date, exceptionsForDate)
        }

        val weeklyRanges = staffWeeklyScheduleRepository.listByStaffId(staffId)
            .filter { it.dayOfWeek == date.dayOfWeek.value }

        return StaffScheduleSnapshot(
            staffId = staffId,
            date = date,
            source = StaffScheduleSource.WEEKLY,
            workRanges = weeklyRanges.filter { it.rangeType == StaffScheduleRangeType.WORK }.map(::toAvailabilityRange),
            breakRanges = weeklyRanges.filter { it.rangeType == StaffScheduleRangeType.BREAK }.map(::toAvailabilityRange)
        )
    }

    override fun getScheduleForRange(
        businessId: UUID,
        staffId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<StaffScheduleSnapshot> {
        require(!endDate.isBefore(startDate)) { "endDate must not be before startDate" }
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return emptyList()

        return generateSequence(startDate) { current ->
            current.plusDays(1).takeIf { !it.isAfter(endDate) }
        }.mapNotNull { date ->
            getScheduleForDate(businessId, staffId, date)
        }.toList()
    }

    private fun fromExceptions(
        staffId: UUID,
        date: LocalDate,
        exceptions: List<StaffScheduleException>
    ): StaffScheduleSnapshot {
        if (exceptions.any { it.rangeType == StaffScheduleRangeType.DAY_OFF }) {
            return StaffScheduleSnapshot(
                staffId = staffId,
                date = date,
                source = StaffScheduleSource.DAY_OFF_EXCEPTION,
                workRanges = emptyList(),
                breakRanges = emptyList()
            )
        }

        return StaffScheduleSnapshot(
            staffId = staffId,
            date = date,
            source = StaffScheduleSource.EXCEPTION,
            workRanges = exceptions.filter { it.rangeType == StaffScheduleRangeType.WORK }.map(::toAvailabilityRange),
            breakRanges = exceptions.filter { it.rangeType == StaffScheduleRangeType.BREAK }.map(::toAvailabilityRange)
        )
    }

    private fun ensureStaffBelongsToBusiness(businessId: UUID, staffId: UUID) =
        staffRepository.findById(businessId, staffId)

    private fun toAvailabilityRange(schedule: StaffWeeklySchedule): AvailabilityTimeRange =
        AvailabilityTimeRange(
            startTime = schedule.startTime,
            endTime = schedule.endTime
        )

    private fun toAvailabilityRange(exception: StaffScheduleException): AvailabilityTimeRange =
        AvailabilityTimeRange(
            startTime = exception.startTime ?: error("Exception startTime is required"),
            endTime = exception.endTime ?: error("Exception endTime is required")
        )
}
