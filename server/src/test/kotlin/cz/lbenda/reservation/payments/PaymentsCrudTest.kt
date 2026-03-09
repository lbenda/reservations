package cz.lbenda.reservation.payments

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
class PaymentsCrudTest {
    private val dsl = TestDatabase.dsl
    private val businessRepository = BusinessRepository(dsl)
    private val paymentRepository = PaymentRepository(dsl)

    @BeforeEach
    fun reset() {
        TestDatabase.resetTenantAccess()
    }

    @AfterAll
    fun closeDatabase() {
        TestDatabase.close()
    }

    @Test
    fun `payment CRUD works`() {
        val business = createBusiness()
        val bookingId = Uuid7.new()

        val created = paymentRepository.create(
            NewPayment(
                id = Uuid7.new(),
                businessId = business.id,
                bookingId = bookingId,
                providerRef = "pay_001",
                providerName = "stripe",
                amount = BigDecimal("1200.00"),
                currency = "CZK",
                status = "pending",
                paidAt = null
            )
        )

        val fetched = paymentRepository.findById(created.id)
        assertNotNull(fetched)
        assertEquals("pay_001", fetched.providerRef)

        val updated = paymentRepository.update(
            created.id,
            PaymentUpdate(
                providerName = "stripe",
                amount = BigDecimal("1200.00"),
                currency = "CZK",
                status = "paid",
                paidAt = OffsetDateTime.now(ZoneOffset.UTC)
            )
        )
        assertNotNull(updated)
        assertEquals("paid", updated.status)

        assertTrue(paymentRepository.delete(created.id))
        assertNull(paymentRepository.findById(created.id))
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
