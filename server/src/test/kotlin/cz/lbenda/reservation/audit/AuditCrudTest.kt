package cz.lbenda.reservation.audit

import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.tenant.BusinessRepository
import cz.lbenda.reservation.tenant.NewBusiness
import cz.lbenda.reservation.tenant.NewUser
import cz.lbenda.reservation.tenant.UserRepository
import cz.lbenda.reservation.util.Uuid7
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuditCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val userRepository = UserRepository(dsl)
    private val auditRepository = AuditRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `audit event CRUD works`() {
        val business = businessRepository.create(
            NewBusiness(
                id = Uuid7.new(),
                slug = "acme-${Uuid7.new().toString().take(8)}",
                name = "Acme Spa",
                timezone = "Europe/Prague",
                currency = "CZK",
                status = "active"
            )
        )
        val user = userRepository.create(
            NewUser(
                id = Uuid7.new(),
                email = "actor@acme.test",
                firstName = "Anna",
                lastName = "Novak",
                phone = null,
                locale = "cs-CZ",
                status = "active"
            )
        )

        val created = auditRepository.create(
            NewAuditEvent(
                id = Uuid7.new(),
                businessId = business.id,
                actorUserId = user.id,
                bookingId = null,
                clientId = null,
                eventType = "booking_created",
                payload = "{\"note\":\"created\"}",
                occurredAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )

        val fetched = auditRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("booking_created", fetched.eventType)

        val updated = auditRepository.update(
            created.id,
            AuditEventUpdate(
                bookingId = Uuid7.new(),
                clientId = null,
                eventType = "booking_updated",
                payload = "{\"note\":\"updated\"}",
                occurredAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )
        assertNotNull(updated)
        assertEquals("booking_updated", updated.eventType)

        assertTrue(auditRepository.delete(created.id))
        assertNull(auditRepository.findById(created.id))
    }
}
