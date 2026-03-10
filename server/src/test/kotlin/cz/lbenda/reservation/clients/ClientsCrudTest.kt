package cz.lbenda.reservation.clients

import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.tenant.*
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
class ClientsCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val clientRepository = ClientRepository(dsl)
    private val consentRepository = ConsentRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `client CRUD works`() {
        val business = createBusiness()

        val created = clientRepository.create(
            NewClient(
                id = Uuid7.new(),
                businessId = business.id,
                email = "client@acme.test",
                phone = "+420123456789",
                firstName = "Eva",
                lastName = "Novak",
                locale = "cs-CZ",
                notes = "VIP",
                status = "active"
            )
        )

        val fetched = clientRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("Eva", fetched.firstName)

        val updated = clientRepository.update(
            created.id,
            ClientUpdate(
                email = "client2@acme.test",
                phone = null,
                firstName = "Eva",
                lastName = "Novak",
                locale = "en-GB",
                notes = "Updated",
                status = "inactive"
            )
        )
        assertNotNull(updated)
        assertEquals("client2@acme.test", updated.email)

        assertTrue(clientRepository.delete(created.id))
        assertNull(clientRepository.findById(created.id))
    }

    @Test
    fun `consent CRUD works`() {
        val business = createBusiness()
        val client = clientRepository.create(
            NewClient(
                id = Uuid7.new(),
                businessId = business.id,
                email = "consent@acme.test",
                phone = null,
                firstName = "Klara",
                lastName = "Novak",
                locale = "cs-CZ",
                notes = null,
                status = "active"
            )
        )

        val created = consentRepository.create(
            NewConsent(
                id = Uuid7.new(),
                businessId = business.id,
                clientId = client.id,
                consentKey = "marketing",
                granted = true,
                grantedAt = OffsetDateTime.now(ZoneOffset.UTC),
                revokedAt = null,
                source = "web"
            )
        )

        val fetched = consentRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("marketing", fetched.consentKey)

        val updated = consentRepository.update(
            created.id,
            ConsentUpdate(
                granted = false,
                grantedAt = fetched.grantedAt,
                revokedAt = OffsetDateTime.now(ZoneOffset.UTC),
                source = "admin"
            )
        )
        assertNotNull(updated)
        assertEquals(false, updated.granted)

        assertTrue(consentRepository.delete(created.id))
        assertNull(consentRepository.findById(created.id))
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
}
