package cz.lbenda.reservation.availability

import java.time.OffsetDateTime
import java.util.UUID

enum class OccupiedTimeSource {
    BOOKING,
    BLOCK,
    BREAK
}

data class AvailabilityRequest(
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val bufferBeforeMinutes: Int = 0,
    val bufferAfterMinutes: Int = 0
) {
    init {
        require(endAt.isAfter(startAt)) { "endAt must be after startAt" }
        require(bufferBeforeMinutes >= 0) { "bufferBeforeMinutes must be greater than or equal to 0" }
        require(bufferAfterMinutes >= 0) { "bufferAfterMinutes must be greater than or equal to 0" }
    }

    val occupiedStartAt: OffsetDateTime = startAt.minusMinutes(bufferBeforeMinutes.toLong())
    val occupiedEndAt: OffsetDateTime = endAt.plusMinutes(bufferAfterMinutes.toLong())
}

data class OccupiedTimeRange(
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val source: OccupiedTimeSource,
    val referenceId: UUID? = null
) {
    init {
        require(endAt.isAfter(startAt)) { "endAt must be after startAt" }
    }
}

interface AvailabilityConflictChecker {
    fun findConflicts(request: AvailabilityRequest, occupiedRanges: List<OccupiedTimeRange>): List<OccupiedTimeRange>

    fun hasConflict(request: AvailabilityRequest, occupiedRanges: List<OccupiedTimeRange>): Boolean =
        findConflicts(request, occupiedRanges).isNotEmpty()
}

class DefaultAvailabilityConflictChecker : AvailabilityConflictChecker {
    override fun findConflicts(
        request: AvailabilityRequest,
        occupiedRanges: List<OccupiedTimeRange>
    ): List<OccupiedTimeRange> =
        occupiedRanges
            .filter { overlaps(request, it) }
            .sortedWith(compareBy<OccupiedTimeRange> { it.startAt }.thenBy { it.endAt }.thenBy { it.source.name })

    private fun overlaps(request: AvailabilityRequest, occupiedRange: OccupiedTimeRange): Boolean =
        request.occupiedStartAt.isBefore(occupiedRange.endAt) &&
            occupiedRange.startAt.isBefore(request.occupiedEndAt)
}
