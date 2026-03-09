package cz.lbenda.reservation.clients

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ConsentRepository(private val dsl: DSLContext) {
    fun create(newConsent: NewConsent): Consent {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(CONSENT)
            .set(CONSENT.ID, newConsent.id)
            .set(CONSENT.BUSINESS_ID, newConsent.businessId)
            .set(CONSENT.CLIENT_ID, newConsent.clientId)
            .set(CONSENT.CONSENT_KEY, newConsent.consentKey)
            .set(CONSENT.GRANTED, newConsent.granted)
            .set(CONSENT.GRANTED_AT, newConsent.grantedAt)
            .set(CONSENT.REVOKED_AT, newConsent.revokedAt)
            .set(CONSENT.SOURCE, newConsent.source)
            .set(CONSENT.CREATED_AT, now)
            .returning(
                CONSENT.ID,
                CONSENT.BUSINESS_ID,
                CONSENT.CLIENT_ID,
                CONSENT.CONSENT_KEY,
                CONSENT.GRANTED,
                CONSENT.GRANTED_AT,
                CONSENT.REVOKED_AT,
                CONSENT.SOURCE,
                CONSENT.CREATED_AT
            )
            .fetchOne() ?: error("Failed to insert consent")

        return Consent(
            id = record.get(CONSENT.ID)!!,
            businessId = record.get(CONSENT.BUSINESS_ID)!!,
            clientId = record.get(CONSENT.CLIENT_ID)!!,
            consentKey = record.get(CONSENT.CONSENT_KEY)!!,
            granted = record.get(CONSENT.GRANTED)!!,
            grantedAt = record.get(CONSENT.GRANTED_AT),
            revokedAt = record.get(CONSENT.REVOKED_AT),
            source = record.get(CONSENT.SOURCE),
            createdAt = record.get(CONSENT.CREATED_AT)!!
        )
    }

    fun findById(id: UUID): Consent? {
        val record = dsl.select(
            CONSENT.ID,
            CONSENT.BUSINESS_ID,
            CONSENT.CLIENT_ID,
            CONSENT.CONSENT_KEY,
            CONSENT.GRANTED,
            CONSENT.GRANTED_AT,
            CONSENT.REVOKED_AT,
            CONSENT.SOURCE,
            CONSENT.CREATED_AT
        )
            .from(CONSENT)
            .where(CONSENT.ID.eq(id))
            .fetchOne() ?: return null

        return Consent(
            id = record.get(CONSENT.ID)!!,
            businessId = record.get(CONSENT.BUSINESS_ID)!!,
            clientId = record.get(CONSENT.CLIENT_ID)!!,
            consentKey = record.get(CONSENT.CONSENT_KEY)!!,
            granted = record.get(CONSENT.GRANTED)!!,
            grantedAt = record.get(CONSENT.GRANTED_AT),
            revokedAt = record.get(CONSENT.REVOKED_AT),
            source = record.get(CONSENT.SOURCE),
            createdAt = record.get(CONSENT.CREATED_AT)!!
        )
    }

    fun update(id: UUID, update: ConsentUpdate): Consent? {
        val record = dsl.update(CONSENT)
            .set(CONSENT.GRANTED, update.granted)
            .set(CONSENT.GRANTED_AT, update.grantedAt)
            .set(CONSENT.REVOKED_AT, update.revokedAt)
            .set(CONSENT.SOURCE, update.source)
            .where(CONSENT.ID.eq(id))
            .returning(
                CONSENT.ID,
                CONSENT.BUSINESS_ID,
                CONSENT.CLIENT_ID,
                CONSENT.CONSENT_KEY,
                CONSENT.GRANTED,
                CONSENT.GRANTED_AT,
                CONSENT.REVOKED_AT,
                CONSENT.SOURCE,
                CONSENT.CREATED_AT
            )
            .fetchOne() ?: return null

        return Consent(
            id = record.get(CONSENT.ID)!!,
            businessId = record.get(CONSENT.BUSINESS_ID)!!,
            clientId = record.get(CONSENT.CLIENT_ID)!!,
            consentKey = record.get(CONSENT.CONSENT_KEY)!!,
            granted = record.get(CONSENT.GRANTED)!!,
            grantedAt = record.get(CONSENT.GRANTED_AT),
            revokedAt = record.get(CONSENT.REVOKED_AT),
            source = record.get(CONSENT.SOURCE),
            createdAt = record.get(CONSENT.CREATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(CONSENT)
            .where(CONSENT.ID.eq(id))
            .execute() > 0
}
