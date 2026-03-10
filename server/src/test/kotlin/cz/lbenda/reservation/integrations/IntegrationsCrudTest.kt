package cz.lbenda.reservation.integrations

import cz.lbenda.reservation.audit.AuditRepository
import cz.lbenda.reservation.audit.NewAuditEvent
import cz.lbenda.reservation.catalog.NewStaff
import cz.lbenda.reservation.catalog.StaffRepository
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
class IntegrationsCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val userRepository = UserRepository(dsl)
    private val staffRepository = StaffRepository(dsl)
    private val auditRepository = AuditRepository(dsl)
    private val webhookEndpointRepository = WebhookEndpointRepository(dsl)
    private val webhookDeliveryRepository = WebhookDeliveryRepository(dsl)
    private val externalCalendarRepository = ExternalCalendarRepository(dsl)
    private val busyBlockRepository = BusyBlockRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `webhook endpoint CRUD works`() {
        val business = createBusiness()

        val created = webhookEndpointRepository.create(
            NewWebhookEndpoint(
                id = Uuid7.new(),
                businessId = business.id,
                endpointKey = "wh_001",
                url = "https://example.test/webhook",
                isActive = true,
                secretRef = "secret-1"
            )
        )

        val fetched = webhookEndpointRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("wh_001", fetched.endpointKey)

        val updated = webhookEndpointRepository.update(
            created.id,
            WebhookEndpointUpdate(
                url = "https://example.test/webhook2",
                isActive = false,
                secretRef = null
            )
        )
        assertNotNull(updated)
        assertEquals(false, updated.isActive)

        assertTrue(webhookEndpointRepository.delete(created.id))
        assertNull(webhookEndpointRepository.findById(created.id))
    }

    @Test
    fun `webhook delivery CRUD works`() {
        val business = createBusiness()
        val user = createUser()
        val event = auditRepository.create(
            NewAuditEvent(
                id = Uuid7.new(),
                businessId = business.id,
                actorUserId = user.id,
                bookingId = null,
                clientId = null,
                eventType = "test_event",
                payload = "{\"ok\":true}",
                occurredAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )
        val endpoint = webhookEndpointRepository.create(
            NewWebhookEndpoint(
                id = Uuid7.new(),
                businessId = business.id,
                endpointKey = "wh_002",
                url = "https://example.test/webhook",
                isActive = true,
                secretRef = null
            )
        )

        val created = webhookDeliveryRepository.create(
            NewWebhookDelivery(
                id = Uuid7.new(),
                businessId = business.id,
                webhookEndpointId = endpoint.id,
                eventId = event.id,
                deliveryKey = "del_001",
                status = "pending",
                attemptCount = 1,
                lastAttemptAt = OffsetDateTime.now(ZoneOffset.UTC),
                responseCode = 200
            )
        )

        val fetched = webhookDeliveryRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("del_001", fetched.deliveryKey)

        val updated = webhookDeliveryRepository.update(
            created.id,
            WebhookDeliveryUpdate(
                status = "delivered",
                attemptCount = 2,
                lastAttemptAt = OffsetDateTime.now(ZoneOffset.UTC),
                responseCode = 204
            )
        )
        assertNotNull(updated)
        assertEquals("delivered", updated.status)

        assertTrue(webhookDeliveryRepository.delete(created.id))
        assertNull(webhookDeliveryRepository.findById(created.id))
    }

    @Test
    fun `external calendar CRUD works`() {
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

        val created = externalCalendarRepository.create(
            NewExternalCalendar(
                id = Uuid7.new(),
                businessId = business.id,
                staffId = staff.id,
                provider = "google",
                providerAccountId = "acct-001",
                syncEnabled = true,
                lastSyncedAt = null
            )
        )

        val fetched = externalCalendarRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("google", fetched.provider)

        val updated = externalCalendarRepository.update(
            created.id,
            ExternalCalendarUpdate(
                provider = "google",
                providerAccountId = "acct-002",
                syncEnabled = false,
                lastSyncedAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )
        assertNotNull(updated)
        assertEquals("acct-002", updated.providerAccountId)

        assertTrue(externalCalendarRepository.delete(created.id))
        assertNull(externalCalendarRepository.findById(created.id))
    }

    @Test
    fun `busy block CRUD works`() {
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
        val calendar = externalCalendarRepository.create(
            NewExternalCalendar(
                id = Uuid7.new(),
                businessId = business.id,
                staffId = staff.id,
                provider = "google",
                providerAccountId = "acct-010",
                syncEnabled = true,
                lastSyncedAt = null
            )
        )

        val created = busyBlockRepository.create(
            NewBusyBlock(
                id = Uuid7.new(),
                businessId = business.id,
                staffId = staff.id,
                externalCalendarId = calendar.id,
                providerEventId = "event-1",
                startAt = OffsetDateTime.now(ZoneOffset.UTC),
                endAt = OffsetDateTime.now(ZoneOffset.UTC).plusHours(1),
                summary = "Busy"
            )
        )

        val fetched = busyBlockRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("event-1", fetched.providerEventId)

        val updated = busyBlockRepository.update(
            created.id,
            BusyBlockUpdate(
                providerEventId = "event-2",
                startAt = fetched.startAt,
                endAt = fetched.endAt.plusMinutes(30),
                summary = "Busy updated"
            )
        )
        assertNotNull(updated)
        assertEquals("event-2", updated.providerEventId)

        assertTrue(busyBlockRepository.delete(created.id))
        assertNull(busyBlockRepository.findById(created.id))
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

    private fun createUser(): User = userRepository.create(
        NewUser(
            id = Uuid7.new(),
            email = "actor-${Uuid7.new().toString().take(8)}@acme.test",
            firstName = "Anna",
            lastName = "Novak",
            phone = null,
            locale = "cs-CZ",
            status = "active"
        )
    )

    private fun createLocation(business: Business): Location {
        return LocationRepository(dsl).create(
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
}
