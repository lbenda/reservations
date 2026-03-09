package cz.lbenda.reservation.packages

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class Package(
    val id: UUID,
    val businessId: UUID,
    val packageCode: String?,
    val name: String,
    val description: String?,
    val totalCredits: Int,
    val validityDays: Int?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewPackage(
    val id: UUID,
    val businessId: UUID,
    val packageCode: String?,
    val name: String,
    val description: String?,
    val totalCredits: Int,
    val validityDays: Int?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean
)

data class PackageUpdate(
    val packageCode: String?,
    val name: String,
    val description: String?,
    val totalCredits: Int,
    val validityDays: Int?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean
)

data class EntitlementEntry(
    val id: UUID,
    val businessId: UUID,
    val clientId: UUID,
    val packageId: UUID,
    val bookingId: UUID?,
    val entryType: String,
    val quantity: Int,
    val effectiveAt: OffsetDateTime,
    val expiresAt: OffsetDateTime?,
    val note: String?,
    val createdAt: OffsetDateTime
)

data class NewEntitlementEntry(
    val id: UUID,
    val businessId: UUID,
    val clientId: UUID,
    val packageId: UUID,
    val bookingId: UUID?,
    val entryType: String,
    val quantity: Int,
    val effectiveAt: OffsetDateTime,
    val expiresAt: OffsetDateTime?,
    val note: String?
)

data class EntitlementUpdate(
    val bookingId: UUID?,
    val entryType: String,
    val quantity: Int,
    val effectiveAt: OffsetDateTime,
    val expiresAt: OffsetDateTime?,
    val note: String?
)
