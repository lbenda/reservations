package cz.lbenda.reservation.availability

import cz.lbenda.reservation.booking.BlockRepository
import cz.lbenda.reservation.booking.BookingRepository
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import java.time.OffsetDateTime
import java.util.UUID

interface StaffOccupiedTimeService {
    fun listOccupiedRanges(
        businessId: UUID,
        staffId: UUID,
        windowStart: OffsetDateTime,
        windowEnd: OffsetDateTime
    ): List<OccupiedTimeRange>
}

class DefaultStaffOccupiedTimeService(
    private val staffRepository: StaffRepository,
    private val bookingRepository: BookingRepository,
    private val blockRepository: BlockRepository,
    private val serviceRepository: ServiceRepository
) : StaffOccupiedTimeService {
    override fun listOccupiedRanges(
        businessId: UUID,
        staffId: UUID,
        windowStart: OffsetDateTime,
        windowEnd: OffsetDateTime
    ): List<OccupiedTimeRange> {
        require(windowEnd.isAfter(windowStart)) { "windowEnd must be after windowStart" }

        val staff = staffRepository.findById(businessId, staffId) ?: return emptyList()

        val bookingRanges = bookingRepository.listOverlapping(
            businessId = businessId,
            staffId = staffId,
            windowStart = windowStart,
            windowEnd = windowEnd
        ).map { booking ->
            val service = serviceRepository.findById(businessId, booking.serviceId)
            OccupiedTimeRange(
                startAt = booking.startAt.minusMinutes((service?.bufferBeforeMinutes ?: 0).toLong()),
                endAt = booking.endAt.plusMinutes((service?.bufferAfterMinutes ?: 0).toLong()),
                source = OccupiedTimeSource.BOOKING,
                referenceId = booking.id
            )
        }

        val blockRanges = blockRepository.listOverlapping(
            businessId = businessId,
            locationId = staff.locationId,
            staffId = staffId,
            windowStart = windowStart,
            windowEnd = windowEnd
        ).map { block ->
            OccupiedTimeRange(
                startAt = block.startAt,
                endAt = block.endAt,
                source = OccupiedTimeSource.BLOCK,
                referenceId = block.id
            )
        }

        return (bookingRanges + blockRanges)
            .sortedWith(compareBy<OccupiedTimeRange> { it.startAt }.thenBy { it.endAt }.thenBy { it.source.name })
    }
}
