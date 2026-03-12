package cz.lbenda.reservation.availability

import cz.lbenda.reservation.booking.BlockRepository
import cz.lbenda.reservation.booking.BookingRepository
import cz.lbenda.reservation.booking.NewBlock
import cz.lbenda.reservation.booking.NewBooking
import cz.lbenda.reservation.catalog.NewService
import cz.lbenda.reservation.catalog.NewStaff
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
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
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StaffOccupiedTimeServiceTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val locationRepository = LocationRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val serviceRepository = ServiceRepository(dsl)
    private val clientRepository = ClientRepository(dsl)
    private val bookingRepository = BookingRepository(dsl)
    private val blockRepository = BlockRepository(dsl)
    private val service = DefaultStaffOccupiedTimeService(
        staffRepository = staffRepository,
        bookingRepository = bookingRepository,
        blockRepository = blockRepository,
        serviceRepository = serviceRepository
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
    fun `service returns booking buffers and relevant blocks as occupied ranges`() {
        val business = createBusiness("occupied")
        val location = createLocation(business)
        val staff = createStaff(business, location, "Eva")
        val client = createClient(business)
        val catalogService = createService(business, bufferBeforeMinutes = 15, bufferAfterMinutes = 10)
        val windowStart = OffsetDateTime.of(2026, 3, 21, 9, 0, 0, 0, ZoneOffset.UTC)
        val windowEnd = OffsetDateTime.of(2026, 3, 21, 14, 0, 0, 0, ZoneOffset.UTC)

        val booking = bookingRepository.create(
            NewBooking(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                serviceId = catalogService.id,
                staffId = staff.id,
                clientId = client.id,
                publicRef = "BK-OCCUPIED",
                status = "confirmed",
                startAt = OffsetDateTime.of(2026, 3, 21, 10, 0, 0, 0, ZoneOffset.UTC),
                endAt = OffsetDateTime.of(2026, 3, 21, 11, 0, 0, 0, ZoneOffset.UTC),
                timezone = "Europe/Prague",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                notes = null,
                clientMessage = null
            )
        )
        val block = blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                staffId = null,
                startAt = OffsetDateTime.of(2026, 3, 21, 12, 0, 0, 0, ZoneOffset.UTC),
                endAt = OffsetDateTime.of(2026, 3, 21, 12, 30, 0, 0, ZoneOffset.UTC),
                reason = "Location maintenance"
            )
        )

        val occupiedRanges = service.listOccupiedRanges(
            businessId = business.id,
            staffId = staff.id,
            windowStart = windowStart,
            windowEnd = windowEnd
        )

        assertEquals(2, occupiedRanges.size)
        assertEquals(booking.id, occupiedRanges[0].referenceId)
        assertEquals(OccupiedTimeSource.BOOKING, occupiedRanges[0].source)
        assertEquals(OffsetDateTime.of(2026, 3, 21, 9, 45, 0, 0, ZoneOffset.UTC), occupiedRanges[0].startAt)
        assertEquals(OffsetDateTime.of(2026, 3, 21, 11, 10, 0, 0, ZoneOffset.UTC), occupiedRanges[0].endAt)
        assertEquals(block.id, occupiedRanges[1].referenceId)
        assertEquals(OccupiedTimeSource.BLOCK, occupiedRanges[1].source)
    }

    @Test
    fun `service returns empty list for staff outside requested business`() {
        val business = createBusiness("main")
        val otherBusiness = createBusiness("other")
        val staff = createStaff(otherBusiness, createLocation(otherBusiness), "Mila")

        val occupiedRanges = service.listOccupiedRanges(
            businessId = business.id,
            staffId = staff.id,
            windowStart = OffsetDateTime.of(2026, 3, 21, 9, 0, 0, 0, ZoneOffset.UTC),
            windowEnd = OffsetDateTime.of(2026, 3, 21, 14, 0, 0, 0, ZoneOffset.UTC)
        )

        assertTrue(occupiedRanges.isEmpty())
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

    private fun createService(business: Business, bufferBeforeMinutes: Int, bufferAfterMinutes: Int) =
        serviceRepository.create(
            NewService(
                id = Uuid7.new(),
                businessId = business.id,
                serviceCode = "SVC-OCCUPIED",
                name = "Massage 60",
                description = null,
                durationMinutes = 60,
                bufferBeforeMinutes = bufferBeforeMinutes,
                bufferAfterMinutes = bufferAfterMinutes,
                minAdvanceMinutes = 120,
                maxAdvanceDays = 30,
                cancellationPolicy = "24 hours notice",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )

    private fun createClient(business: Business) =
        clientRepository.create(
            NewClient(
                id = Uuid7.new(),
                businessId = business.id,
                email = "booking@acme.test",
                phone = null,
                firstName = "Eva",
                lastName = "Novak",
                locale = "cs-CZ",
                notes = null,
                status = "active"
            )
        )
}
