package cz.lbenda.reservation.packages

import cz.lbenda.reservation.clients.ClientRepository
import cz.lbenda.reservation.clients.NewClient
import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.tenant.BusinessRepository
import cz.lbenda.reservation.tenant.NewBusiness
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
class PackagesCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val clientRepository = ClientRepository(dsl)
    private val packageRepository = PackageRepository(dsl)
    private val entitlementRepository = EntitlementRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `package CRUD works`() {
        val business = createBusiness()

        val created = packageRepository.create(
            NewPackage(
                id = Uuid7.new(),
                businessId = business.id,
                packageCode = "PKG-001",
                name = "Massage Pack",
                description = "5x massage",
                totalCredits = 5,
                validityDays = 180,
                priceAmount = BigDecimal("4500.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )

        val fetched = packageRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("PKG-001", fetched.packageCode)

        val updated = packageRepository.update(
            created.id,
            PackageUpdate(
                packageCode = "PKG-002",
                name = "Massage Pack+",
                description = "6x massage",
                totalCredits = 6,
                validityDays = 365,
                priceAmount = BigDecimal("5200.00"),
                priceCurrency = "CZK",
                isActive = false
            )
        )
        assertNotNull(updated)
        assertEquals("PKG-002", updated.packageCode)

        assertTrue(packageRepository.delete(created.id))
        assertNull(packageRepository.findById(created.id))
    }

    @Test
    fun `entitlement CRUD works`() {
        val business = createBusiness()
        val client = clientRepository.create(
            NewClient(
                id = Uuid7.new(),
                businessId = business.id,
                email = "entitlement@acme.test",
                phone = null,
                firstName = "Lenka",
                lastName = "Novak",
                locale = "cs-CZ",
                notes = null,
                status = "active"
            )
        )
        val pkg = packageRepository.create(
            NewPackage(
                id = Uuid7.new(),
                businessId = business.id,
                packageCode = "PKG-ENT",
                name = "Small Pack",
                description = null,
                totalCredits = 3,
                validityDays = 90,
                priceAmount = BigDecimal("2500.00"),
                priceCurrency = "CZK",
                isActive = true
            )
        )

        val created = entitlementRepository.create(
            NewEntitlementEntry(
                id = Uuid7.new(),
                businessId = business.id,
                clientId = client.id,
                packageId = pkg.id,
                bookingId = null,
                entryType = "purchase",
                quantity = 3,
                effectiveAt = OffsetDateTime.now(ZoneOffset.UTC),
                expiresAt = null,
                note = "Initial purchase"
            )
        )

        val fetched = entitlementRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("purchase", fetched.entryType)

        val updated = entitlementRepository.update(
            created.id,
            EntitlementUpdate(
                bookingId = Uuid7.new(),
                entryType = "redemption",
                quantity = -1,
                effectiveAt = fetched.effectiveAt,
                expiresAt = fetched.expiresAt,
                note = "Redeemed 1 session"
            )
        )
        assertNotNull(updated)
        assertEquals("redemption", updated.entryType)

        assertTrue(entitlementRepository.delete(created.id))
        assertNull(entitlementRepository.findById(created.id))
    }

    private fun createBusiness() = businessRepository.create(
        NewBusiness(
            id = Uuid7.new(),
            slug = "acme-${Uuid7.new().toString().take(8)}",
            name = "Acme Spa",
            timezone = "Europe/Prague",
            currency = "CZK",
            status = "active"
        )
    )
}
