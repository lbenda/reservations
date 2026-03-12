package cz.lbenda.reservation.availability

import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AvailabilityConflictCheckerTest {
    private val checker = DefaultAvailabilityConflictChecker()

    @Test
    fun `request without overlap has no conflict`() {
        val request = requestAt(9, 0, 10, 0)
        val occupied = listOf(
            occupiedAt(10, 0, 11, 0, OccupiedTimeSource.BOOKING)
        )

        assertFalse(checker.hasConflict(request, occupied))
        assertTrue(checker.findConflicts(request, occupied).isEmpty())
    }

    @Test
    fun `request overlapping booking is blocked`() {
        val request = requestAt(9, 30, 10, 30)
        val occupied = listOf(
            occupiedAt(10, 0, 11, 0, OccupiedTimeSource.BOOKING)
        )

        val conflicts = checker.findConflicts(request, occupied)

        assertEquals(1, conflicts.size)
        assertEquals(OccupiedTimeSource.BOOKING, conflicts.single().source)
    }

    @Test
    fun `buffer before can create conflict without direct booking overlap`() {
        val request = AvailabilityRequest(
            startAt = at(10, 0),
            endAt = at(11, 0),
            bufferBeforeMinutes = 15
        )
        val occupied = listOf(
            occupiedAt(9, 45, 10, 0, OccupiedTimeSource.BLOCK)
        )

        assertTrue(checker.hasConflict(request, occupied))
    }

    @Test
    fun `buffer after can create conflict without direct booking overlap`() {
        val request = AvailabilityRequest(
            startAt = at(9, 0),
            endAt = at(10, 0),
            bufferAfterMinutes = 20
        )
        val occupied = listOf(
            occupiedAt(10, 15, 10, 45, OccupiedTimeSource.BOOKING)
        )

        assertTrue(checker.hasConflict(request, occupied))
    }

    @Test
    fun `conflicts are returned in deterministic order`() {
        val request = AvailabilityRequest(
            startAt = at(9, 0),
            endAt = at(10, 0),
            bufferAfterMinutes = 45
        )
        val laterId = UUID.randomUUID()
        val earlierId = UUID.randomUUID()
        val occupied = listOf(
            OccupiedTimeRange(
                startAt = at(10, 30),
                endAt = at(11, 0),
                source = OccupiedTimeSource.BLOCK,
                referenceId = laterId
            ),
            OccupiedTimeRange(
                startAt = at(10, 5),
                endAt = at(10, 20),
                source = OccupiedTimeSource.BOOKING,
                referenceId = earlierId
            )
        )

        val conflicts = checker.findConflicts(request, occupied)

        assertEquals(listOf(earlierId, laterId), conflicts.map { it.referenceId })
    }

    private fun requestAt(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) = AvailabilityRequest(
        startAt = at(startHour, startMinute),
        endAt = at(endHour, endMinute)
    )

    private fun occupiedAt(
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
        source: OccupiedTimeSource
    ) = OccupiedTimeRange(
        startAt = at(startHour, startMinute),
        endAt = at(endHour, endMinute),
        source = source
    )

    private fun at(hour: Int, minute: Int): OffsetDateTime =
        OffsetDateTime.of(2026, 3, 16, hour, minute, 0, 0, ZoneOffset.UTC)
}
