package cz.lbenda.reservation.catalog

import cz.lbenda.reservation.db.TestDatabase
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceValidationTest {
    private val service = DefaultServiceCatalogService(ServiceRepository(TestDatabase.dsl))

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
        SampleCatalogFixtures.createBusiness(TestDatabase.dsl)
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `valid command passes validation`() {
        service.create(
            SampleCatalogFixtures.businessId,
            SampleCatalogFixtures.validCommand()
        )
    }

    @Test
    fun `duration must be positive`() {
        assertSingleFieldError("durationMinutes", SampleCatalogFixtures.validCommand(durationMinutes = 0))
    }

    @Test
    fun `buffer before must be non negative`() {
        assertSingleFieldError("bufferBeforeMinutes", SampleCatalogFixtures.validCommand(bufferBeforeMinutes = -1))
    }

    @Test
    fun `buffer after must be non negative`() {
        assertSingleFieldError("bufferAfterMinutes", SampleCatalogFixtures.validCommand(bufferAfterMinutes = -1))
    }

    @Test
    fun `min advance must be non negative`() {
        assertSingleFieldError("minAdvanceMinutes", SampleCatalogFixtures.validCommand(minAdvanceMinutes = -1))
    }

    @Test
    fun `max advance must be non negative`() {
        assertSingleFieldError("maxAdvanceDays", SampleCatalogFixtures.validCommand(maxAdvanceDays = -1))
    }

    @Test
    fun `price must be non negative`() {
        assertSingleFieldError("priceAmount", SampleCatalogFixtures.validCommand(priceAmount = BigDecimal("-1.00")))
    }

    private fun assertSingleFieldError(expectedField: String, command: ServiceCommand) {
        val exception = assertFailsWith<ServiceValidationException> {
            service.create(SampleCatalogFixtures.businessId, command)
        }
        assertEquals(expectedField, exception.errors.single().field)
    }
}
