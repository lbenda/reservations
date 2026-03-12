package cz.lbenda.reservation.availability

import cz.lbenda.reservation.catalog.NewStaff
import cz.lbenda.reservation.catalog.NewStaffScheduleException
import cz.lbenda.reservation.catalog.NewStaffWeeklySchedule
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffScheduleRangeType
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
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
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StaffAvailabilityScheduleServiceTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val locationRepository = LocationRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val weeklyScheduleRepository = StaffWeeklyScheduleRepository(dsl)
    private val exceptionRepository = StaffScheduleExceptionRepository(dsl)
    private val service = DefaultStaffAvailabilityScheduleService(
        staffRepository = staffRepository,
        staffWeeklyScheduleRepository = weeklyScheduleRepository,
        staffScheduleExceptionRepository = exceptionRepository
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
    fun `weekly schedule is returned when no exception exists`() {
        val business = createBusiness("weekly")
        val staff = createStaff(business)

        weeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 1,
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(17, 0)
            )
        )
        weeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 1,
                rangeType = StaffScheduleRangeType.BREAK,
                startTime = LocalTime.of(12, 0),
                endTime = LocalTime.of(13, 0)
            )
        )

        val snapshot = service.getScheduleForDate(
            businessId = business.id,
            staffId = staff.id,
            date = LocalDate.of(2026, 3, 16)
        )

        assertNotNull(snapshot)
        assertEquals(StaffScheduleSource.WEEKLY, snapshot.source)
        assertEquals(1, snapshot.workRanges.size)
        assertEquals(LocalTime.of(9, 0), snapshot.workRanges.first().startTime)
        assertEquals(1, snapshot.breakRanges.size)
        assertEquals(LocalTime.of(12, 0), snapshot.breakRanges.first().startTime)
    }

    @Test
    fun `exceptions override weekly schedule for a date`() {
        val business = createBusiness("exception")
        val staff = createStaff(business)

        weeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 1,
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(17, 0)
            )
        )
        exceptionRepository.create(
            NewStaffScheduleException(
                id = Uuid7.new(),
                staffId = staff.id,
                exceptionDate = LocalDate.of(2026, 3, 16),
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(14, 0),
                note = "Special hours"
            )
        )
        exceptionRepository.create(
            NewStaffScheduleException(
                id = Uuid7.new(),
                staffId = staff.id,
                exceptionDate = LocalDate.of(2026, 3, 16),
                rangeType = StaffScheduleRangeType.BREAK,
                startTime = LocalTime.of(12, 30),
                endTime = LocalTime.of(13, 0),
                note = "Lunch"
            )
        )

        val snapshot = service.getScheduleForDate(
            businessId = business.id,
            staffId = staff.id,
            date = LocalDate.of(2026, 3, 16)
        )

        assertNotNull(snapshot)
        assertEquals(StaffScheduleSource.EXCEPTION, snapshot.source)
        assertEquals(1, snapshot.workRanges.size)
        assertEquals(LocalTime.of(10, 0), snapshot.workRanges.first().startTime)
        assertEquals(1, snapshot.breakRanges.size)
        assertEquals(LocalTime.of(12, 30), snapshot.breakRanges.first().startTime)
    }

    @Test
    fun `day off exception clears weekly schedule`() {
        val business = createBusiness("day-off")
        val staff = createStaff(business)

        weeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 1,
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(17, 0)
            )
        )
        exceptionRepository.create(
            NewStaffScheduleException(
                id = Uuid7.new(),
                staffId = staff.id,
                exceptionDate = LocalDate.of(2026, 3, 16),
                rangeType = StaffScheduleRangeType.DAY_OFF,
                startTime = null,
                endTime = null,
                note = "Vacation"
            )
        )

        val snapshot = service.getScheduleForDate(
            businessId = business.id,
            staffId = staff.id,
            date = LocalDate.of(2026, 3, 16)
        )

        assertNotNull(snapshot)
        assertEquals(StaffScheduleSource.DAY_OFF_EXCEPTION, snapshot.source)
        assertTrue(snapshot.workRanges.isEmpty())
        assertTrue(snapshot.breakRanges.isEmpty())
    }

    @Test
    fun `range query returns one snapshot per requested date`() {
        val business = createBusiness("range")
        val staff = createStaff(business)

        weeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 1,
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(17, 0)
            )
        )
        weeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = Uuid7.new(),
                staffId = staff.id,
                dayOfWeek = 2,
                rangeType = StaffScheduleRangeType.WORK,
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(16, 0)
            )
        )

        val snapshots = service.getScheduleForRange(
            businessId = business.id,
            staffId = staff.id,
            startDate = LocalDate.of(2026, 3, 16),
            endDate = LocalDate.of(2026, 3, 17)
        )

        assertEquals(2, snapshots.size)
        assertEquals(LocalDate.of(2026, 3, 16), snapshots[0].date)
        assertEquals(LocalDate.of(2026, 3, 17), snapshots[1].date)
    }

    @Test
    fun `service returns null for staff outside requested business`() {
        val business = createBusiness("main")
        val otherBusiness = createBusiness("other")
        val staff = createStaff(otherBusiness)

        val snapshot = service.getScheduleForDate(
            businessId = business.id,
            staffId = staff.id,
            date = LocalDate.of(2026, 3, 16)
        )

        assertNull(snapshot)
    }

    private fun createBusiness(slugPrefix: String): Business =
        businessRepository.create(
            NewBusiness(
                id = Uuid7.new(),
                slug = "$slugPrefix-${Uuid7.new().toString().take(8)}",
                name = "Acme Spa",
                timezone = "Europe/Prague",
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
                timezone = "Europe/Prague",
                status = "active"
            )
        )

    private fun createStaff(business: Business) =
        staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = createLocation(business).id,
                displayName = "Eva Staff",
                email = "eva@acme.test",
                phone = null,
                bio = null,
                status = "active"
            )
        )
}
