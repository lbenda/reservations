package cz.lbenda.reservation.booking

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class Booking(
    val id: UUID,
    val businessId: UUID,
    val locationId: UUID,
    val serviceId: UUID,
    val staffId: UUID,
    val clientId: UUID,
    val publicRef: String,
    val status: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val timezone: String,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val notes: String?,
    val clientMessage: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewBooking(
    val id: UUID,
    val businessId: UUID,
    val locationId: UUID,
    val serviceId: UUID,
    val staffId: UUID,
    val clientId: UUID,
    val publicRef: String,
    val status: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val timezone: String,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val notes: String?,
    val clientMessage: String?
)

data class BookingUpdate(
    val status: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val timezone: String,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val notes: String?,
    val clientMessage: String?
)

data class Block(
    val id: UUID,
    val businessId: UUID,
    val locationId: UUID,
    val staffId: UUID?,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val reason: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewBlock(
    val id: UUID,
    val businessId: UUID,
    val locationId: UUID,
    val staffId: UUID?,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val reason: String?
)

data class BlockUpdate(
    val staffId: UUID?,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val reason: String?
)
