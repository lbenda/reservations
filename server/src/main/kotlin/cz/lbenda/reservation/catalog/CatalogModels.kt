package cz.lbenda.reservation.catalog

import java.time.OffsetDateTime
import java.util.UUID
import java.math.BigDecimal

data class Staff(
    val id: UUID,
    val businessId: UUID,
    val locationId: UUID,
    val displayName: String,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val status: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewStaff(
    val id: UUID,
    val businessId: UUID,
    val locationId: UUID,
    val displayName: String,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val status: String
)

data class StaffUpdate(
    val displayName: String,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val status: String
)

data class Service(
    val id: UUID,
    val businessId: UUID,
    val serviceCode: String?,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val bufferBeforeMinutes: Int?,
    val bufferAfterMinutes: Int?,
    val minAdvanceMinutes: Int?,
    val maxAdvanceDays: Int?,
    val cancellationPolicy: String?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewService(
    val id: UUID,
    val businessId: UUID,
    val serviceCode: String?,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val bufferBeforeMinutes: Int?,
    val bufferAfterMinutes: Int?,
    val minAdvanceMinutes: Int?,
    val maxAdvanceDays: Int?,
    val cancellationPolicy: String?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean
)

data class ServiceUpdate(
    val serviceCode: String?,
    val name: String,
    val description: String?,
    val durationMinutes: Int,
    val bufferBeforeMinutes: Int?,
    val bufferAfterMinutes: Int?,
    val minAdvanceMinutes: Int?,
    val maxAdvanceDays: Int?,
    val cancellationPolicy: String?,
    val priceAmount: BigDecimal,
    val priceCurrency: String,
    val isActive: Boolean
)

data class StaffService(
    val id: UUID,
    val staffId: UUID,
    val serviceId: UUID,
    val staffServiceKey: String?,
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewStaffService(
    val id: UUID,
    val staffId: UUID,
    val serviceId: UUID,
    val staffServiceKey: String?,
    val isActive: Boolean
)

data class StaffServiceUpdate(
    val staffServiceKey: String?,
    val isActive: Boolean
)
