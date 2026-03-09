package cz.lbenda.reservation.tenant

import cz.lbenda.reservation.db.TestDatabase
import cz.lbenda.reservation.util.Uuid7
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import java.time.OffsetDateTime
import java.time.ZoneOffset

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val locationRepository = LocationRepository(dsl)
    private val userRepository = UserRepository(dsl)
    private val roleRepository = RoleRepository(dsl)
    private val businessUserRepository = BusinessUserRepository(dsl)
    private val apiKeyRepository = ApiKeyRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `business CRUD works`() {
        val created = businessRepository.create(
            NewBusiness(
                id = Uuid7.new(),
                slug = "acme",
                name = "Acme Spa",
                timezone = "Europe/Prague",
                currency = "CZK",
                status = "active"
            )
        )

        val fetched = businessRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("acme", fetched.slug)

        val updated = businessRepository.update(
            created.id,
            BusinessUpdate(
                slug = "acme-updated",
                name = "Acme Spa Updated",
                timezone = "Europe/Prague",
                currency = "CZK",
                status = "paused"
            )
        )
        assertNotNull(updated)
        assertEquals("acme-updated", updated.slug)

        assertTrue(businessRepository.delete(created.id))
        assertNull(businessRepository.findById(created.id))
    }

    @Test
    fun `location CRUD works`() {
        val business = createBusiness()

        val created = locationRepository.create(
            NewLocation(
                id = Uuid7.new(),
                businessId = business.id,
                slug = "main",
                name = "Main Branch",
                addressLine1 = "Street 1",
                addressLine2 = null,
                city = "Prague",
                postalCode = "11000",
                countryCode = "CZ",
                phone = "+420111222333",
                email = "main@acme.test",
                timezone = null,
                status = "active"
            )
        )

        val fetched = locationRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("main", fetched.slug)

        val updated = locationRepository.update(
            created.id,
            LocationUpdate(
                slug = "main-upd",
                name = "Main Branch Updated",
                addressLine1 = "Street 2",
                addressLine2 = "Building B",
                city = "Prague",
                postalCode = "11000",
                countryCode = "CZ",
                phone = null,
                email = "main2@acme.test",
                timezone = "Europe/Prague",
                status = "inactive"
            )
        )
        assertNotNull(updated)
        assertEquals("main-upd", updated.slug)

        assertTrue(locationRepository.delete(created.id))
        assertNull(locationRepository.findById(created.id))
    }

    @Test
    fun `user CRUD works`() {
        val created = userRepository.create(
            NewUser(
                id = Uuid7.new(),
                email = "owner@acme.test",
                firstName = "Ada",
                lastName = "Lovelace",
                phone = null,
                locale = "cs-CZ",
                status = "active"
            )
        )

        val fetched = userRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("Ada", fetched.firstName)

        val updated = userRepository.update(
            created.id,
            UserUpdate(
                email = "owner2@acme.test",
                firstName = "Ada",
                lastName = "Lovelace",
                phone = "+420999888777",
                locale = "en-GB",
                status = "active",
                lastLoginAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )
        assertNotNull(updated)
        assertEquals("owner2@acme.test", updated.email)

        assertTrue(userRepository.delete(created.id))
        assertNull(userRepository.findById(created.id))
    }

    @Test
    fun `role CRUD works`() {
        val created = roleRepository.create(
            NewRole(
                id = Uuid7.new(),
                code = "OWNER",
                name = "Owner",
                description = "Business owner"
            )
        )

        val fetched = roleRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("OWNER", fetched.code)

        val updated = roleRepository.update(
            created.id,
            RoleUpdate(
                code = "MANAGER",
                name = "Manager",
                description = "Business manager"
            )
        )
        assertNotNull(updated)
        assertEquals("MANAGER", updated.code)

        assertTrue(roleRepository.delete(created.id))
        assertNull(roleRepository.findById(created.id))
    }

    @Test
    fun `business user CRUD works`() {
        val business = createBusiness()
        val user = createUser()
        val role = createRole("OWNER")

        val created = businessUserRepository.create(
            NewBusinessUser(
                id = Uuid7.new(),
                businessId = business.id,
                userId = user.id,
                roleId = role.id,
                businessUserKey = "invite-001",
                status = "active"
            )
        )

        val fetched = businessUserRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals(role.id, fetched.roleId)

        val newRole = createRole("MANAGER")
        val updated = businessUserRepository.update(
            created.id,
            BusinessUserUpdate(
                roleId = newRole.id,
                businessUserKey = "invite-002",
                status = "inactive"
            )
        )
        assertNotNull(updated)
        assertEquals(newRole.id, updated.roleId)

        assertTrue(businessUserRepository.delete(created.id))
        assertNull(businessUserRepository.findById(created.id))
    }

    @Test
    fun `api key CRUD works`() {
        val business = createBusiness()
        val created = apiKeyRepository.create(
            NewApiKey(
                id = Uuid7.new(),
                businessId = business.id,
                keyId = "key_001",
                name = "Primary key"
            )
        )

        val fetched = apiKeyRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("key_001", fetched.keyId)

        val updated = apiKeyRepository.update(
            created.id,
            ApiKeyUpdate(
                name = "Secondary key",
                lastUsedAt = OffsetDateTime.now(ZoneOffset.UTC),
                revokedAt = null
            )
        )
        assertNotNull(updated)
        assertEquals("Secondary key", updated.name)

        assertTrue(apiKeyRepository.delete(created.id))
        assertNull(apiKeyRepository.findById(created.id))
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
            email = "user-${Uuid7.new().toString().take(8)}@acme.test",
            firstName = "Ada",
            lastName = "Lovelace",
            phone = null,
            locale = "cs-CZ",
            status = "active"
        )
    )

    private fun createRole(code: String): Role = roleRepository.create(
        NewRole(
            id = Uuid7.new(),
            code = code,
            name = code.lowercase().replaceFirstChar { it.uppercase() },
            description = null
        )
    )
}
