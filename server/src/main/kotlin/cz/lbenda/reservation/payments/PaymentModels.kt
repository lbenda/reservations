package cz.lbenda.reservation.payments

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class Payment(
    val id: UUID,
    val businessId: UUID,
    val bookingId: UUID,
    val providerRef: String,
    val providerName: String,
    val amount: BigDecimal,
    val currency: String,
    val status: String,
    val paidAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewPayment(
    val id: UUID,
    val businessId: UUID,
    val bookingId: UUID,
    val providerRef: String,
    val providerName: String,
    val amount: BigDecimal,
    val currency: String,
    val status: String,
    val paidAt: OffsetDateTime?
)

data class PaymentUpdate(
    val providerName: String,
    val amount: BigDecimal,
    val currency: String,
    val status: String,
    val paidAt: OffsetDateTime?
)
