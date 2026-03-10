package cz.lbenda.reservation.audit

import java.time.OffsetDateTime
import java.util.UUID

data class AuditEvent(
    val id: UUID,
    val businessId: UUID,
    val actorUserId: UUID,
    val bookingId: UUID?,
    val clientId: UUID?,
    val eventType: String,
    val payload: String?,
    val occurredAt: OffsetDateTime,
    val createdAt: OffsetDateTime
)

data class NewAuditEvent(
    val id: UUID,
    val businessId: UUID,
    val actorUserId: UUID,
    val bookingId: UUID?,
    val clientId: UUID?,
    val eventType: String,
    val payload: String?,
    val occurredAt: OffsetDateTime
)

data class AuditEventUpdate(
    val bookingId: UUID?,
    val clientId: UUID?,
    val eventType: String,
    val payload: String?,
    val occurredAt: OffsetDateTime
)
