package cz.lbenda.reservation.booking

import cz.lbenda.reservation.catalog.NewService
import cz.lbenda.reservation.catalog.NewStaff
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.clients.ClientRepository
import cz.lbenda.reservation.clients.NewClient
import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.tenant.*
import cz.lbenda.reservation.util.Uuid7
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val locationRepository = LocationRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val serviceRepository = ServiceRepository(dsl)
    private val clientRepository = ClientRepository(dsl)
    private val bookingRepository = BookingRepository(dsl)
    private val blockRepository = BlockRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `booking CRUD works`() {
        val business = createBusiness()
        val location = createLocation(business)
        val staff = staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                displayName = "Eva",
                email = null,
                phone = null,
                bio = null,
                status = "active"
            )
        )
        val service = serviceRepository.create(
            NewService(
                id = Uuid7.new(),
                businessId = business.id,
                serviceCode = "SVC-BOOK",
                name = "Massage 60",
                description = null,
                durationMinutes = 60,
                bufferBeforeMinutes = 5,
                bufferAfterMinutes = 10,
                minAdvanceMinutes = 120,
                maxAdvanceDays = 30,
                cancellationPolicy = "24 hours notice",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )
        val client = clientRepository.create(
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

        val start = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1)
        val end = start.plusHours(1)

        val created = bookingRepository.create(
            NewBooking(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                serviceId = service.id,
                staffId = staff.id,
                clientId = client.id,
                publicRef = "BK-001",
                status = "reserved",
                startAt = start,
                endAt = end,
                timezone = "Europe/Prague",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                notes = "Internal note",
                clientMessage = "Please be on time"
            )
        )

        val fetched = bookingRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("BK-001", fetched.publicRef)

        val updated = bookingRepository.update(
            created.id,
            BookingUpdate(
                status = "confirmed",
                startAt = start,
                endAt = end.plusMinutes(15),
                timezone = "Europe/Prague",
                priceAmount = BigDecimal("1200.00"),
                priceCurrency = "CZK",
                notes = "Updated note",
                clientMessage = "Updated message"
            )
        )
        assertNotNull(updated)
        assertEquals("confirmed", updated.status)

        assertTrue(bookingRepository.delete(created.id))
        assertNull(bookingRepository.findById(created.id))
    }

    @Test
    fun `block CRUD works`() {
        val business = createBusiness()
        val location = createLocation(business)
        val staff = staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                displayName = "Eva",
                email = null,
                phone = null,
                bio = null,
                status = "active"
            )
        )
        val start = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1)
        val end = start.plusHours(2)

        val created = blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                staffId = staff.id,
                startAt = start,
                endAt = end,
                reason = "Vacation"
            )
        )

        val fetched = blockRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("Vacation", fetched.reason)

        val updated = blockRepository.update(
            created.id,
            BlockUpdate(
                staffId = null,
                startAt = start.plusHours(1),
                endAt = end.plusHours(1),
                reason = "Maintenance"
            )
        )
        assertNotNull(updated)
        assertEquals("Maintenance", updated.reason)

        assertTrue(blockRepository.delete(created.id))
        assertNull(blockRepository.findById(created.id))
    }

    @Test
    fun `listOverlapping returns only active overlapping bookings for the requested staff`() {
        val business = createBusiness()
        val location = createLocation(business)
        val staff = createStaff(business, location, "Eva")
        val otherStaff = createStaff(business, location, "Mila")
        val service = createService(business)
        val client = createClient(business)
        val windowStart = OffsetDateTime.of(2026, 3, 20, 10, 0, 0, 0, ZoneOffset.UTC)
        val windowEnd = windowStart.plusHours(1)

        val matchingBooking = bookingRepository.create(
            newBooking(
                business = business,
                location = location,
                service = service,
                staff = staff,
                client = client,
                publicRef = "BK-MATCH",
                status = "confirmed",
                startAt = windowStart.plusMinutes(15),
                endAt = windowStart.plusMinutes(45)
            )
        )
        bookingRepository.create(
            newBooking(
                business = business,
                location = location,
                service = service,
                staff = staff,
                client = client,
                publicRef = "BK-CANCELED",
                status = "canceled",
                startAt = windowStart.plusMinutes(20),
                endAt = windowStart.plusMinutes(50)
            )
        )
        bookingRepository.create(
            newBooking(
                business = business,
                location = location,
                service = service,
                staff = otherStaff,
                client = client,
                publicRef = "BK-OTHER-STAFF",
                status = "confirmed",
                startAt = windowStart.plusMinutes(20),
                endAt = windowStart.plusMinutes(50)
            )
        )
        bookingRepository.create(
            newBooking(
                business = business,
                location = location,
                service = service,
                staff = staff,
                client = client,
                publicRef = "BK-NO-OVERLAP",
                status = "confirmed",
                startAt = windowEnd,
                endAt = windowEnd.plusMinutes(30)
            )
        )

        val overlapping = bookingRepository.listOverlapping(
            businessId = business.id,
            staffId = staff.id,
            windowStart = windowStart,
            windowEnd = windowEnd
        )

        assertEquals(listOf(matchingBooking.id), overlapping.map { it.id })
    }

    @Test
    fun `listOverlapping returns staff and global location blocks`() {
        val business = createBusiness()
        val location = createLocation(business)
        val otherLocation = createLocation(business)
        val staff = createStaff(business, location, "Eva")
        val otherStaff = createStaff(business, location, "Mila")
        val windowStart = OffsetDateTime.of(2026, 3, 20, 10, 0, 0, 0, ZoneOffset.UTC)
        val windowEnd = windowStart.plusHours(1)

        val staffBlock = blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                staffId = staff.id,
                startAt = windowStart.plusMinutes(10),
                endAt = windowStart.plusMinutes(30),
                reason = "Staff blocked"
            )
        )
        val globalBlock = blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                staffId = null,
                startAt = windowStart.plusMinutes(35),
                endAt = windowStart.plusMinutes(55),
                reason = "Location blocked"
            )
        )
        blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                staffId = otherStaff.id,
                startAt = windowStart.plusMinutes(15),
                endAt = windowStart.plusMinutes(25),
                reason = "Other staff"
            )
        )
        blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = otherLocation.id,
                staffId = null,
                startAt = windowStart.plusMinutes(15),
                endAt = windowStart.plusMinutes(25),
                reason = "Other location"
            )
        )
        blockRepository.create(
            NewBlock(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                staffId = staff.id,
                startAt = windowEnd,
                endAt = windowEnd.plusMinutes(20),
                reason = "Boundary only"
            )
        )

        val overlapping = blockRepository.listOverlapping(
            businessId = business.id,
            locationId = location.id,
            staffId = staff.id,
            windowStart = windowStart,
            windowEnd = windowEnd
        )

        assertEquals(listOf(staffBlock.id, globalBlock.id), overlapping.map { it.id })
    }

    private fun createBusiness(): Business = businessRepository.create(
        NewBusiness(
            id = Uuid7.new(),
            slug = "acme-${Uuid7.new().toString().take(8)}",
            name = "Acme Spa",
            timezone = "Europe/Prague",
            currency = "CZK",
            status = "active"
        )
    )

    private fun createLocation(business: Business): Location = locationRepository.create(
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
                email = null,
                phone = null,
                bio = null,
                status = "active"
            )
        )

    private fun createService(business: Business) =
        serviceRepository.create(
            NewService(
                id = Uuid7.new(),
                businessId = business.id,
                serviceCode = "SVC-BOOK",
                name = "Massage 60",
                description = null,
                durationMinutes = 60,
                bufferBeforeMinutes = 5,
                bufferAfterMinutes = 10,
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

    private fun newBooking(
        business: Business,
        location: Location,
        service: cz.lbenda.reservation.catalog.Service,
        staff: cz.lbenda.reservation.catalog.Staff,
        client: cz.lbenda.reservation.clients.Client,
        publicRef: String,
        status: String,
        startAt: OffsetDateTime,
        endAt: OffsetDateTime
    ) = NewBooking(
        id = Uuid7.new(),
        businessId = business.id,
        locationId = location.id,
        serviceId = service.id,
        staffId = staff.id,
        clientId = client.id,
        publicRef = publicRef,
        status = status,
        startAt = startAt,
        endAt = endAt,
        timezone = "Europe/Prague",
        priceAmount = BigDecimal("1200.00"),
        priceCurrency = "CZK",
        notes = null,
        clientMessage = null
    )
}
