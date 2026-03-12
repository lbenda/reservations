package cz.lbenda.reservation.availability

import cz.lbenda.reservation.booking.BlockRepository
import cz.lbenda.reservation.booking.BookingRepository
import cz.lbenda.reservation.booking.NewBooking
import cz.lbenda.reservation.catalog.NewService
import cz.lbenda.reservation.catalog.NewStaff
import cz.lbenda.reservation.catalog.NewStaffService
import cz.lbenda.reservation.catalog.NewStaffWeeklySchedule
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffScheduleRangeType
import cz.lbenda.reservation.catalog.StaffServiceRepository
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import cz.lbenda.reservation.clients.ClientRepository
import cz.lbenda.reservation.clients.NewClient
import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.tenant.Business
import cz.lbenda.reservation.tenant.BusinessRepository
import cz.lbenda.reservation.tenant.Location
import cz.lbenda.reservation.tenant.LocationRepository
import cz.lbenda.reservation.tenant.NewBusiness
import cz.lbenda.reservation.tenant.NewLocation
import cz.lbenda.reservation.util.Uuid7
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvailabilitySlotServiceTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val locationRepository = LocationRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val serviceRepository = ServiceRepository(dsl)
    private val staffServiceRepository = StaffServiceRepository(dsl)
    private val staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(dsl)
    private val staffScheduleExceptionRepository = StaffScheduleExceptionRepository(dsl)
    private val clientRepository = ClientRepository(dsl)
    private val bookingRepository = BookingRepository(dsl)
    private val blockRepository = BlockRepository(dsl)
    private val scheduleService = DefaultStaffAvailabilityScheduleService(
        staffRepository = staffRepository,
        staffWeeklyScheduleRepository = staffWeeklyScheduleRepository,
        staffScheduleExceptionRepository = staffScheduleExceptionRepository
    )
    private val occupiedTimeService = DefaultStaffOccupiedTimeService(
        staffRepository = staffRepository,
        bookingRepository = bookingRepository,
        blockRepository = blockRepository,
        serviceRepository = serviceRepository
    )
    private val slotService = DefaultAvailabilitySlotService(
        staffRepository = staffRepository,
        staffServiceRepository = staffServiceRepository,
        serviceRepository = serviceRepository,
        staffAvailabilityScheduleService = scheduleService,
        staffOccupiedTimeService = occupiedTimeService,
        availabilityConflictChecker = DefaultAvailabilityConflictChecker()
    )

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `slot generation excludes breaks and occupied bookings`() {
        val business = createBusiness("slots")
        val location = createLocation(business)
        val staff = createStaff(business, location, "Eva")
        val client = createClient(business)
        val service = createService(business, "Massage 30", 30)
        assignStaffToService(staff.id, service.id)
        createWeeklySchedule(staff.id, StaffScheduleRangeType.WORK, LocalTime.of(9, 0), LocalTime.of(12, 0))
        createWeeklySchedule(staff.id, StaffScheduleRangeType.BREAK, LocalTime.of(10, 0), LocalTime.of(10, 30))
        bookingRepository.create(
            NewBooking(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                serviceId = service.id,
                staffId = staff.id,
                clientId = client.id,
                publicRef = "BK-SLOT",
                status = "confirmed",
                startAt = OffsetDateTime.of(2026, 3, 23, 11, 0, 0, 0, ZoneOffset.UTC),
                endAt = OffsetDateTime.of(2026, 3, 23, 11, 30, 0, 0, ZoneOffset.UTC),
                timezone = "UTC",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                notes = null,
                clientMessage = null
            )
        )

        val slots = slotService.generateSlots(
            AvailabilitySlotQuery(
                businessId = business.id,
                serviceId = service.id,
                staffId = staff.id,
                startDate = LocalDate.of(2026, 3, 23),
                endDate = LocalDate.of(2026, 3, 23),
                timezone = "UTC",
                slotIntervalMinutes = 30
            )
        )

        assertEquals(
            listOf("09:00", "09:30", "10:30", "11:30"),
            slots.map { it.startAt.toLocalTime().toString().substring(0, 5) }
        )
        assertTrue(slots.all { it.staffId == staff.id })
    }

    @Test
    fun `slot generation without staffId returns only assigned active staff`() {
        val business = createBusiness("assignments")
        val location = createLocation(business)
        val assignedStaff = createStaff(business, location, "Eva")
        val unassignedStaff = createStaff(business, location, "Mila")
        val service = createService(business, "Massage 30", 30)
        assignStaffToService(assignedStaff.id, service.id)
        createWeeklySchedule(assignedStaff.id, StaffScheduleRangeType.WORK, LocalTime.of(9, 0), LocalTime.of(10, 0))
        createWeeklySchedule(unassignedStaff.id, StaffScheduleRangeType.WORK, LocalTime.of(9, 0), LocalTime.of(10, 0))

        val slots = slotService.generateSlots(
            AvailabilitySlotQuery(
                businessId = business.id,
                serviceId = service.id,
                startDate = LocalDate.of(2026, 3, 23),
                endDate = LocalDate.of(2026, 3, 23),
                timezone = "UTC",
                slotIntervalMinutes = 30
            )
        )

        assertEquals(2, slots.size)
        assertTrue(slots.all { it.staffId == assignedStaff.id })
    }

    @Test
    fun `slot generation skips nonexistent local times during spring forward DST`() {
        val business = createBusiness("dst")
        val location = createLocation(business)
        val staff = createStaff(business, location, "Eva")
        val service = createService(
            business = business,
            name = "DST Massage",
            durationMinutes = 30
        )
        assignStaffToService(staff.id, service.id)
        createWeeklySchedule(
            staffId = staff.id,
            rangeType = StaffScheduleRangeType.WORK,
            startTime = LocalTime.of(1, 0),
            endTime = LocalTime.of(4, 0),
            dayOfWeek = 7
        )

        val slots = slotService.generateSlots(
            AvailabilitySlotQuery(
                businessId = business.id,
                serviceId = service.id,
                staffId = staff.id,
                startDate = LocalDate.of(2026, 3, 29),
                endDate = LocalDate.of(2026, 3, 29),
                timezone = "Europe/Prague",
                slotIntervalMinutes = 30
            )
        )

        assertEquals(
            listOf("01:00", "01:30", "03:00", "03:30"),
            slots.map { it.startAt.atZoneSameInstant(ZoneId.of("Europe/Prague")).toLocalTime().toString().substring(0, 5) }
        )
    }

    @Test
    fun `slot generation requires buffers to stay inside working range`() {
        val business = createBusiness("buffers")
        val location = createLocation(business)
        val staff = createStaff(business, location, "Eva")
        val service = createService(
            business = business,
            name = "Buffered Massage",
            durationMinutes = 30,
            bufferBeforeMinutes = 15,
            bufferAfterMinutes = 15
        )
        assignStaffToService(staff.id, service.id)
        createWeeklySchedule(
            staffId = staff.id,
            rangeType = StaffScheduleRangeType.WORK,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0)
        )

        val slots = slotService.generateSlots(
            AvailabilitySlotQuery(
                businessId = business.id,
                serviceId = service.id,
                staffId = staff.id,
                startDate = LocalDate.of(2026, 3, 23),
                endDate = LocalDate.of(2026, 3, 23),
                timezone = "UTC",
                slotIntervalMinutes = 15
            )
        )

        assertEquals(listOf("09:15"), slots.map { it.startAt.toLocalTime().toString().substring(0, 5) })
    }

    private fun createBusiness(slugPrefix: String): Business =
        businessRepository.create(
            NewBusiness(
                id = Uuid7.new(),
                slug = "$slugPrefix-${Uuid7.new().toString().take(8)}",
                name = "Acme Spa",
                timezone = "UTC",
                currency = "CZK",
                status = "active"
            )
        )

    private fun createLocation(business: Business): Location =
        locationRepository.create(
            NewLocation(
                id = Uuid7.new(),
                businessId = business.id,
                slug = "main-${Uuid7.new().toString().take(8)}",
                name = "Main Branch",
                addressLine1 = "Street 1",
                addressLine2 = null,
                city = "Prague",
                postalCode = "11000",
                countryCode = "CZ",
                phone = null,
                email = null,
                timezone = "UTC",
                status = "active"
            )
        )

    private fun createStaff(business: Business, location: Location, displayName: String) =
        staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                displayName = displayName,
                email = "$displayName@acme.test",
                phone = null,
                bio = null,
                status = "active"
            )
        )

    private fun createService(
        business: Business,
        name: String,
        durationMinutes: Int,
        bufferBeforeMinutes: Int = 0,
        bufferAfterMinutes: Int = 0
    ) =
        serviceRepository.create(
            NewService(
                id = Uuid7.new(),
                businessId = business.id,
                serviceCode = name.uppercase().replace(" ", "-"),
                name = name,
                description = null,
                durationMinutes = durationMinutes,
                bufferBeforeMinutes = bufferBeforeMinutes,
                bufferAfterMinutes = bufferAfterMinutes,
                minAdvanceMinutes = 0,
                maxAdvanceDays = 30,
                cancellationPolicy = null,
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )

    private fun assignStaffToService(staffId: UUID, serviceId: UUID) {
        staffServiceRepository.create(
            NewStaffService(
                id = Uuid7.new(),
                staffId = staffId,
                serviceId = serviceId,
                staffServiceKey = null,
                isActive = true
            )
        )
    }

    private fun createWeeklySchedule(
        staffId: UUID,
        rangeType: StaffScheduleRangeType,
        startTime: LocalTime,
        endTime: LocalTime,
        dayOfWeek: Int = 1
    ) {
        staffWeeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staffId,
                dayOfWeek = dayOfWeek,
                rangeType = rangeType,
                startTime = startTime,
                endTime = endTime
            )
        )
    }

    private fun createClient(business: Business) =
        clientRepository.create(
            NewClient(
                id = Uuid7.new(),
                businessId = business.id,
                email = "booking@acme.test",
                phone = null,
                firstName = "Eva",
                lastName = "Novak",
                locale = ZoneId.of("UTC").id,
                notes = null,
                status = "active"
            )
        )
}
