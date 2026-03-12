package cz.lbenda.reservation.catalog

import java.time.OffsetDateTime
import java.time.LocalDate
import java.time.LocalTime
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

enum class StaffScheduleRangeType {
    WORK,
    BREAK,
    DAY_OFF
}

data class StaffWeeklySchedule(
    val id: UUID,
    val staffId: UUID,
    val dayOfWeek: Int,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewStaffWeeklySchedule(
    val id: UUID,
    val staffId: UUID,
    val dayOfWeek: Int,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class StaffWeeklyScheduleUpdate(
    val dayOfWeek: Int,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class StaffScheduleException(
    val id: UUID,
    val staffId: UUID,
    val exceptionDate: LocalDate,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val note: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewStaffScheduleException(
    val id: UUID,
    val staffId: UUID,
    val exceptionDate: LocalDate,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val note: String?
)

data class StaffScheduleExceptionUpdate(
    val exceptionDate: LocalDate,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val note: String?
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
