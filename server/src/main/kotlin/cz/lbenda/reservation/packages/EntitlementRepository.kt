package cz.lbenda.reservation.packages

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class EntitlementRepository(private val dsl: DSLContext) {
    fun create(newEntry: NewEntitlementEntry): EntitlementEntry {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(ENTITLEMENT_LEDGER)
            .set(ENTITLEMENT_LEDGER.ID, newEntry.id)
            .set(ENTITLEMENT_LEDGER.BUSINESS_ID, newEntry.businessId)
            .set(ENTITLEMENT_LEDGER.CLIENT_ID, newEntry.clientId)
            .set(ENTITLEMENT_LEDGER.PACKAGE_ID, newEntry.packageId)
            .set(ENTITLEMENT_LEDGER.BOOKING_ID, newEntry.bookingId)
            .set(ENTITLEMENT_LEDGER.ENTRY_TYPE, newEntry.entryType)
            .set(ENTITLEMENT_LEDGER.QUANTITY, newEntry.quantity)
            .set(ENTITLEMENT_LEDGER.EFFECTIVE_AT, newEntry.effectiveAt)
            .set(ENTITLEMENT_LEDGER.EXPIRES_AT, newEntry.expiresAt)
            .set(ENTITLEMENT_LEDGER.NOTE, newEntry.note)
            .set(ENTITLEMENT_LEDGER.CREATED_AT, now)
            .returning(
                ENTITLEMENT_LEDGER.ID,
                ENTITLEMENT_LEDGER.BUSINESS_ID,
                ENTITLEMENT_LEDGER.CLIENT_ID,
                ENTITLEMENT_LEDGER.PACKAGE_ID,
                ENTITLEMENT_LEDGER.BOOKING_ID,
                ENTITLEMENT_LEDGER.ENTRY_TYPE,
                ENTITLEMENT_LEDGER.QUANTITY,
                ENTITLEMENT_LEDGER.EFFECTIVE_AT,
                ENTITLEMENT_LEDGER.EXPIRES_AT,
                ENTITLEMENT_LEDGER.NOTE,
                ENTITLEMENT_LEDGER.CREATED_AT
            )
            .fetchOne() ?: error("Failed to insert entitlement entry")

        return EntitlementEntry(
            id = record.get(ENTITLEMENT_LEDGER.ID)!!,
            businessId = record.get(ENTITLEMENT_LEDGER.BUSINESS_ID)!!,
            clientId = record.get(ENTITLEMENT_LEDGER.CLIENT_ID)!!,
            packageId = record.get(ENTITLEMENT_LEDGER.PACKAGE_ID)!!,
            bookingId = record.get(ENTITLEMENT_LEDGER.BOOKING_ID),
            entryType = record.get(ENTITLEMENT_LEDGER.ENTRY_TYPE)!!,
            quantity = record.get(ENTITLEMENT_LEDGER.QUANTITY)!!,
            effectiveAt = record.get(ENTITLEMENT_LEDGER.EFFECTIVE_AT)!!,
            expiresAt = record.get(ENTITLEMENT_LEDGER.EXPIRES_AT),
            note = record.get(ENTITLEMENT_LEDGER.NOTE),
            createdAt = record.get(ENTITLEMENT_LEDGER.CREATED_AT)!!
        )
    }

    fun findById(id: UUID): EntitlementEntry? {
        val record = dsl.select(
            ENTITLEMENT_LEDGER.ID,
            ENTITLEMENT_LEDGER.BUSINESS_ID,
            ENTITLEMENT_LEDGER.CLIENT_ID,
            ENTITLEMENT_LEDGER.PACKAGE_ID,
            ENTITLEMENT_LEDGER.BOOKING_ID,
            ENTITLEMENT_LEDGER.ENTRY_TYPE,
            ENTITLEMENT_LEDGER.QUANTITY,
            ENTITLEMENT_LEDGER.EFFECTIVE_AT,
            ENTITLEMENT_LEDGER.EXPIRES_AT,
            ENTITLEMENT_LEDGER.NOTE,
            ENTITLEMENT_LEDGER.CREATED_AT
        )
            .from(ENTITLEMENT_LEDGER)
            .where(ENTITLEMENT_LEDGER.ID.eq(id))
            .fetchOne() ?: return null

        return EntitlementEntry(
            id = record.get(ENTITLEMENT_LEDGER.ID)!!,
            businessId = record.get(ENTITLEMENT_LEDGER.BUSINESS_ID)!!,
            clientId = record.get(ENTITLEMENT_LEDGER.CLIENT_ID)!!,
            packageId = record.get(ENTITLEMENT_LEDGER.PACKAGE_ID)!!,
            bookingId = record.get(ENTITLEMENT_LEDGER.BOOKING_ID),
            entryType = record.get(ENTITLEMENT_LEDGER.ENTRY_TYPE)!!,
            quantity = record.get(ENTITLEMENT_LEDGER.QUANTITY)!!,
            effectiveAt = record.get(ENTITLEMENT_LEDGER.EFFECTIVE_AT)!!,
            expiresAt = record.get(ENTITLEMENT_LEDGER.EXPIRES_AT),
            note = record.get(ENTITLEMENT_LEDGER.NOTE),
            createdAt = record.get(ENTITLEMENT_LEDGER.CREATED_AT)!!
        )
    }

    fun update(id: UUID, update: EntitlementUpdate): EntitlementEntry? {
        val record = dsl.update(ENTITLEMENT_LEDGER)
            .set(ENTITLEMENT_LEDGER.BOOKING_ID, update.bookingId)
            .set(ENTITLEMENT_LEDGER.ENTRY_TYPE, update.entryType)
            .set(ENTITLEMENT_LEDGER.QUANTITY, update.quantity)
            .set(ENTITLEMENT_LEDGER.EFFECTIVE_AT, update.effectiveAt)
            .set(ENTITLEMENT_LEDGER.EXPIRES_AT, update.expiresAt)
            .set(ENTITLEMENT_LEDGER.NOTE, update.note)
            .where(ENTITLEMENT_LEDGER.ID.eq(id))
            .returning(
                ENTITLEMENT_LEDGER.ID,
                ENTITLEMENT_LEDGER.BUSINESS_ID,
                ENTITLEMENT_LEDGER.CLIENT_ID,
                ENTITLEMENT_LEDGER.PACKAGE_ID,
                ENTITLEMENT_LEDGER.BOOKING_ID,
                ENTITLEMENT_LEDGER.ENTRY_TYPE,
                ENTITLEMENT_LEDGER.QUANTITY,
                ENTITLEMENT_LEDGER.EFFECTIVE_AT,
                ENTITLEMENT_LEDGER.EXPIRES_AT,
                ENTITLEMENT_LEDGER.NOTE,
                ENTITLEMENT_LEDGER.CREATED_AT
            )
            .fetchOne() ?: return null

        return EntitlementEntry(
            id = record.get(ENTITLEMENT_LEDGER.ID)!!,
            businessId = record.get(ENTITLEMENT_LEDGER.BUSINESS_ID)!!,
            clientId = record.get(ENTITLEMENT_LEDGER.CLIENT_ID)!!,
            packageId = record.get(ENTITLEMENT_LEDGER.PACKAGE_ID)!!,
            bookingId = record.get(ENTITLEMENT_LEDGER.BOOKING_ID),
            entryType = record.get(ENTITLEMENT_LEDGER.ENTRY_TYPE)!!,
            quantity = record.get(ENTITLEMENT_LEDGER.QUANTITY)!!,
            effectiveAt = record.get(ENTITLEMENT_LEDGER.EFFECTIVE_AT)!!,
            expiresAt = record.get(ENTITLEMENT_LEDGER.EXPIRES_AT),
            note = record.get(ENTITLEMENT_LEDGER.NOTE),
            createdAt = record.get(ENTITLEMENT_LEDGER.CREATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(ENTITLEMENT_LEDGER)
            .where(ENTITLEMENT_LEDGER.ID.eq(id))
            .execute() > 0
}
