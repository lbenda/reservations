package cz.lbenda.reservation.tenant

import java.time.OffsetDateTime
import java.util.UUID

data class BusinessUser(
    val id: UUID,
    val businessId: UUID,
    val userId: UUID,
    val roleId: UUID,
    val businessUserKey: String?,
    val status: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewBusinessUser(
    val id: UUID,
    val businessId: UUID,
    val userId: UUID,
    val roleId: UUID,
    val businessUserKey: String?,
    val status: String
)

data class BusinessUserUpdate(
    val roleId: UUID,
    val businessUserKey: String?,
    val status: String
)
