package cz.lbenda.reservation.clients

import java.time.OffsetDateTime
import java.util.UUID

data class Client(
    val id: UUID,
    val businessId: UUID,
    val email: String?,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val locale: String?,
    val notes: String?,
    val status: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewClient(
    val id: UUID,
    val businessId: UUID,
    val email: String?,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val locale: String?,
    val notes: String?,
    val status: String
)

data class ClientUpdate(
    val email: String?,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val locale: String?,
    val notes: String?,
    val status: String
)

data class Consent(
    val id: UUID,
    val businessId: UUID,
    val clientId: UUID,
    val consentKey: String,
    val granted: Boolean,
    val grantedAt: OffsetDateTime?,
    val revokedAt: OffsetDateTime?,
    val source: String?,
    val createdAt: OffsetDateTime
)

data class NewConsent(
    val id: UUID,
    val businessId: UUID,
    val clientId: UUID,
    val consentKey: String,
    val granted: Boolean,
    val grantedAt: OffsetDateTime?,
    val revokedAt: OffsetDateTime?,
    val source: String?
)

data class ConsentUpdate(
    val granted: Boolean,
    val grantedAt: OffsetDateTime?,
    val revokedAt: OffsetDateTime?,
    val source: String?
)
