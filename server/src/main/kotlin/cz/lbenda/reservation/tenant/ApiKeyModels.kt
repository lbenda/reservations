package cz.lbenda.reservation.tenant

import java.time.OffsetDateTime
import java.util.UUID

data class ApiKey(
    val id: UUID,
    val businessId: UUID,
    val keyId: String,
    val name: String,
    val lastUsedAt: OffsetDateTime?,
    val revokedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime
)

data class NewApiKey(
    val id: UUID,
    val businessId: UUID,
    val keyId: String,
    val name: String
)

data class ApiKeyUpdate(
    val name: String,
    val lastUsedAt: OffsetDateTime?,
    val revokedAt: OffsetDateTime?
)
