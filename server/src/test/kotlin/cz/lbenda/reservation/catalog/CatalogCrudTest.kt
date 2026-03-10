package cz.lbenda.reservation.catalog

import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.tenant.*
import cz.lbenda.reservation.util.Uuid7
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val locationRepository = LocationRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val serviceRepository = ServiceRepository(dsl)
    private val staffServiceRepository = StaffServiceRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `service CRUD works`() {
        val business = createBusiness()

        val created = serviceRepository.create(
            NewService(
                id = Uuid7.new(),
                businessId = business.id,
                serviceCode = "SVC-001",
                name = "Massage 60",
                description = "Relaxing massage",
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

        val fetched = serviceRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("Massage 60", fetched.name)

        val updated = serviceRepository.update(
            created.id,
            ServiceUpdate(
                serviceCode = "SVC-002",
                name = "Massage 60+",
                description = "Relaxing massage updated",
                durationMinutes = 70,
                bufferBeforeMinutes = 10,
                bufferAfterMinutes = 10,
                minAdvanceMinutes = 60,
                maxAdvanceDays = 14,
                cancellationPolicy = "12 hours notice",
                priceAmount = BigDecimal("1300.00"),
                priceCurrency = "CZK",
                isActive = false
            )
        )
        assertNotNull(updated)
        assertEquals("SVC-002", updated.serviceCode)

        assertTrue(serviceRepository.delete(created.id))
        assertNull(serviceRepository.findById(created.id))
    }

    @Test
    fun `staff CRUD works`() {
        val business = createBusiness()
        val location = createLocation(business)

        val created = staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                displayName = "Eva Staff",
                email = "eva@acme.test",
                phone = "+420123456789",
                bio = "Senior therapist",
                status = "active"
            )
        )

        val fetched = staffRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("Eva Staff", fetched.displayName)

        val updated = staffRepository.update(
            created.id,
            StaffUpdate(
                displayName = "Eva Staff Updated",
                email = null,
                phone = "+420987654321",
                bio = "Lead therapist",
                status = "inactive"
            )
        )
        assertNotNull(updated)
        assertEquals("Eva Staff Updated", updated.displayName)

        assertTrue(staffRepository.delete(created.id))
        assertNull(staffRepository.findById(created.id))
    }

    @Test
    fun `staff service CRUD works`() {
        val business = createBusiness()
        val location = createLocation(business)
        val staff = staffRepository.create(
            NewStaff(
                id = Uuid7.new(),
                businessId = business.id,
                locationId = location.id,
                displayName = "Staff One",
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
                serviceCode = "SVC-003",
                name = "Massage 30",
                description = null,
                durationMinutes = 30,
                bufferBeforeMinutes = null,
                bufferAfterMinutes = null,
                minAdvanceMinutes = null,
                maxAdvanceDays = null,
                cancellationPolicy = null,
                priceAmount = BigDecimal("600.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )

        val created = staffServiceRepository.create(
            NewStaffService(
                id = Uuid7.new(),
                staffId = staff.id,
                serviceId = service.id,
                staffServiceKey = "SS-001",
                isActive = true
            )
        )

        val fetched = staffServiceRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("SS-001", fetched.staffServiceKey)

        val updated = staffServiceRepository.update(
            created.id,
            StaffServiceUpdate(
                staffServiceKey = "SS-002",
                isActive = false
            )
        )
        assertNotNull(updated)
        assertEquals("SS-002", updated.staffServiceKey)

        assertTrue(staffServiceRepository.delete(created.id))
        assertNull(staffServiceRepository.findById(created.id))
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
}
