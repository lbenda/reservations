package cz.lbenda.reservation.audit

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class AuditRepository(private val dsl: DSLContext) {
    fun create(newEvent: NewAuditEvent): AuditEvent {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(AUDIT_EVENT)
            .set(AUDIT_EVENT.ID, newEvent.id)
            .set(AUDIT_EVENT.BUSINESS_ID, newEvent.businessId)
            .set(AUDIT_EVENT.ACTOR_USER_ID, newEvent.actorUserId)
            .set(AUDIT_EVENT.BOOKING_ID, newEvent.bookingId)
            .set(AUDIT_EVENT.CLIENT_ID, newEvent.clientId)
            .set(AUDIT_EVENT.EVENT_TYPE, newEvent.eventType)
            .set(AUDIT_EVENT.PAYLOAD, newEvent.payload)
            .set(AUDIT_EVENT.OCCURRED_AT, newEvent.occurredAt)
            .set(AUDIT_EVENT.CREATED_AT, now)
            .returning(
                AUDIT_EVENT.ID,
                AUDIT_EVENT.BUSINESS_ID,
                AUDIT_EVENT.ACTOR_USER_ID,
                AUDIT_EVENT.BOOKING_ID,
                AUDIT_EVENT.CLIENT_ID,
                AUDIT_EVENT.EVENT_TYPE,
                AUDIT_EVENT.PAYLOAD,
                AUDIT_EVENT.OCCURRED_AT,
                AUDIT_EVENT.CREATED_AT
            )
            .fetchOne() ?: error("Failed to insert audit event")

        return AuditEvent(
            id = record.get(AUDIT_EVENT.ID)!!,
            businessId = record.get(AUDIT_EVENT.BUSINESS_ID)!!,
            actorUserId = record.get(AUDIT_EVENT.ACTOR_USER_ID)!!,
            bookingId = record.get(AUDIT_EVENT.BOOKING_ID),
            clientId = record.get(AUDIT_EVENT.CLIENT_ID),
            eventType = record.get(AUDIT_EVENT.EVENT_TYPE)!!,
            payload = record.get(AUDIT_EVENT.PAYLOAD),
            occurredAt = record.get(AUDIT_EVENT.OCCURRED_AT)!!,
            createdAt = record.get(AUDIT_EVENT.CREATED_AT)!!
        )
    }

    fun findById(id: UUID): AuditEvent? {
        val record = dsl.select(
            AUDIT_EVENT.ID,
            AUDIT_EVENT.BUSINESS_ID,
            AUDIT_EVENT.ACTOR_USER_ID,
            AUDIT_EVENT.BOOKING_ID,
            AUDIT_EVENT.CLIENT_ID,
            AUDIT_EVENT.EVENT_TYPE,
            AUDIT_EVENT.PAYLOAD,
            AUDIT_EVENT.OCCURRED_AT,
            AUDIT_EVENT.CREATED_AT
        )
            .from(AUDIT_EVENT)
            .where(AUDIT_EVENT.ID.eq(id))
            .fetchOne() ?: return null

        return AuditEvent(
            id = record.get(AUDIT_EVENT.ID)!!,
            businessId = record.get(AUDIT_EVENT.BUSINESS_ID)!!,
            actorUserId = record.get(AUDIT_EVENT.ACTOR_USER_ID)!!,
            bookingId = record.get(AUDIT_EVENT.BOOKING_ID),
            clientId = record.get(AUDIT_EVENT.CLIENT_ID),
            eventType = record.get(AUDIT_EVENT.EVENT_TYPE)!!,
            payload = record.get(AUDIT_EVENT.PAYLOAD),
            occurredAt = record.get(AUDIT_EVENT.OCCURRED_AT)!!,
            createdAt = record.get(AUDIT_EVENT.CREATED_AT)!!
        )
    }

    fun update(id: UUID, update: AuditEventUpdate): AuditEvent? {
        val record = dsl.update(AUDIT_EVENT)
            .set(AUDIT_EVENT.BOOKING_ID, update.bookingId)
            .set(AUDIT_EVENT.CLIENT_ID, update.clientId)
            .set(AUDIT_EVENT.EVENT_TYPE, update.eventType)
            .set(AUDIT_EVENT.PAYLOAD, update.payload)
            .set(AUDIT_EVENT.OCCURRED_AT, update.occurredAt)
            .where(AUDIT_EVENT.ID.eq(id))
            .returning(
                AUDIT_EVENT.ID,
                AUDIT_EVENT.BUSINESS_ID,
                AUDIT_EVENT.ACTOR_USER_ID,
                AUDIT_EVENT.BOOKING_ID,
                AUDIT_EVENT.CLIENT_ID,
                AUDIT_EVENT.EVENT_TYPE,
                AUDIT_EVENT.PAYLOAD,
                AUDIT_EVENT.OCCURRED_AT,
                AUDIT_EVENT.CREATED_AT
            )
            .fetchOne() ?: return null

        return AuditEvent(
            id = record.get(AUDIT_EVENT.ID)!!,
            businessId = record.get(AUDIT_EVENT.BUSINESS_ID)!!,
            actorUserId = record.get(AUDIT_EVENT.ACTOR_USER_ID)!!,
            bookingId = record.get(AUDIT_EVENT.BOOKING_ID),
            clientId = record.get(AUDIT_EVENT.CLIENT_ID),
            eventType = record.get(AUDIT_EVENT.EVENT_TYPE)!!,
            payload = record.get(AUDIT_EVENT.PAYLOAD),
            occurredAt = record.get(AUDIT_EVENT.OCCURRED_AT)!!,
            createdAt = record.get(AUDIT_EVENT.CREATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(AUDIT_EVENT)
            .where(AUDIT_EVENT.ID.eq(id))
            .execute() > 0
}
