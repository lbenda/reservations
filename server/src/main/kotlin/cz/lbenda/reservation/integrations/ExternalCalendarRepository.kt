package cz.lbenda.reservation.integrations

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ExternalCalendarRepository(private val dsl: DSLContext) {
    fun create(newCalendar: NewExternalCalendar): ExternalCalendar {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(EXTERNAL_CALENDAR)
            .set(EXTERNAL_CALENDAR.ID, newCalendar.id)
            .set(EXTERNAL_CALENDAR.BUSINESS_ID, newCalendar.businessId)
            .set(EXTERNAL_CALENDAR.STAFF_ID, newCalendar.staffId)
            .set(EXTERNAL_CALENDAR.PROVIDER, newCalendar.provider)
            .set(EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID, newCalendar.providerAccountId)
            .set(EXTERNAL_CALENDAR.SYNC_ENABLED, newCalendar.syncEnabled)
            .set(EXTERNAL_CALENDAR.LAST_SYNCED_AT, newCalendar.lastSyncedAt)
            .set(EXTERNAL_CALENDAR.CREATED_AT, now)
            .set(EXTERNAL_CALENDAR.UPDATED_AT, now)
            .returning(
                EXTERNAL_CALENDAR.ID,
                EXTERNAL_CALENDAR.BUSINESS_ID,
                EXTERNAL_CALENDAR.STAFF_ID,
                EXTERNAL_CALENDAR.PROVIDER,
                EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID,
                EXTERNAL_CALENDAR.SYNC_ENABLED,
                EXTERNAL_CALENDAR.LAST_SYNCED_AT,
                EXTERNAL_CALENDAR.CREATED_AT,
                EXTERNAL_CALENDAR.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert external calendar")

        return ExternalCalendar(
            id = record.get(EXTERNAL_CALENDAR.ID)!!,
            businessId = record.get(EXTERNAL_CALENDAR.BUSINESS_ID)!!,
            staffId = record.get(EXTERNAL_CALENDAR.STAFF_ID)!!,
            provider = record.get(EXTERNAL_CALENDAR.PROVIDER)!!,
            providerAccountId = record.get(EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID)!!,
            syncEnabled = record.get(EXTERNAL_CALENDAR.SYNC_ENABLED)!!,
            lastSyncedAt = record.get(EXTERNAL_CALENDAR.LAST_SYNCED_AT),
            createdAt = record.get(EXTERNAL_CALENDAR.CREATED_AT)!!,
            updatedAt = record.get(EXTERNAL_CALENDAR.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): ExternalCalendar? {
        val record = dsl.select(
            EXTERNAL_CALENDAR.ID,
            EXTERNAL_CALENDAR.BUSINESS_ID,
            EXTERNAL_CALENDAR.STAFF_ID,
            EXTERNAL_CALENDAR.PROVIDER,
            EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID,
            EXTERNAL_CALENDAR.SYNC_ENABLED,
            EXTERNAL_CALENDAR.LAST_SYNCED_AT,
            EXTERNAL_CALENDAR.CREATED_AT,
            EXTERNAL_CALENDAR.UPDATED_AT
        )
            .from(EXTERNAL_CALENDAR)
            .where(EXTERNAL_CALENDAR.ID.eq(id))
            .fetchOne() ?: return null

        return ExternalCalendar(
            id = record.get(EXTERNAL_CALENDAR.ID)!!,
            businessId = record.get(EXTERNAL_CALENDAR.BUSINESS_ID)!!,
            staffId = record.get(EXTERNAL_CALENDAR.STAFF_ID)!!,
            provider = record.get(EXTERNAL_CALENDAR.PROVIDER)!!,
            providerAccountId = record.get(EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID)!!,
            syncEnabled = record.get(EXTERNAL_CALENDAR.SYNC_ENABLED)!!,
            lastSyncedAt = record.get(EXTERNAL_CALENDAR.LAST_SYNCED_AT),
            createdAt = record.get(EXTERNAL_CALENDAR.CREATED_AT)!!,
            updatedAt = record.get(EXTERNAL_CALENDAR.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: ExternalCalendarUpdate): ExternalCalendar? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(EXTERNAL_CALENDAR)
            .set(EXTERNAL_CALENDAR.PROVIDER, update.provider)
            .set(EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID, update.providerAccountId)
            .set(EXTERNAL_CALENDAR.SYNC_ENABLED, update.syncEnabled)
            .set(EXTERNAL_CALENDAR.LAST_SYNCED_AT, update.lastSyncedAt)
            .set(EXTERNAL_CALENDAR.UPDATED_AT, now)
            .where(EXTERNAL_CALENDAR.ID.eq(id))
            .returning(
                EXTERNAL_CALENDAR.ID,
                EXTERNAL_CALENDAR.BUSINESS_ID,
                EXTERNAL_CALENDAR.STAFF_ID,
                EXTERNAL_CALENDAR.PROVIDER,
                EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID,
                EXTERNAL_CALENDAR.SYNC_ENABLED,
                EXTERNAL_CALENDAR.LAST_SYNCED_AT,
                EXTERNAL_CALENDAR.CREATED_AT,
                EXTERNAL_CALENDAR.UPDATED_AT
            )
            .fetchOne() ?: return null

        return ExternalCalendar(
            id = record.get(EXTERNAL_CALENDAR.ID)!!,
            businessId = record.get(EXTERNAL_CALENDAR.BUSINESS_ID)!!,
            staffId = record.get(EXTERNAL_CALENDAR.STAFF_ID)!!,
            provider = record.get(EXTERNAL_CALENDAR.PROVIDER)!!,
            providerAccountId = record.get(EXTERNAL_CALENDAR.PROVIDER_ACCOUNT_ID)!!,
            syncEnabled = record.get(EXTERNAL_CALENDAR.SYNC_ENABLED)!!,
            lastSyncedAt = record.get(EXTERNAL_CALENDAR.LAST_SYNCED_AT),
            createdAt = record.get(EXTERNAL_CALENDAR.CREATED_AT)!!,
            updatedAt = record.get(EXTERNAL_CALENDAR.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(EXTERNAL_CALENDAR)
            .where(EXTERNAL_CALENDAR.ID.eq(id))
            .execute() > 0
}
