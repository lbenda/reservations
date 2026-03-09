package cz.lbenda.reservation.integrations

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class BusyBlockRepository(private val dsl: DSLContext) {
    fun create(newBlock: NewBusyBlock): BusyBlock {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(BUSY_BLOCK)
            .set(BUSY_BLOCK.ID, newBlock.id)
            .set(BUSY_BLOCK.BUSINESS_ID, newBlock.businessId)
            .set(BUSY_BLOCK.STAFF_ID, newBlock.staffId)
            .set(BUSY_BLOCK.EXTERNAL_CALENDAR_ID, newBlock.externalCalendarId)
            .set(BUSY_BLOCK.PROVIDER_EVENT_ID, newBlock.providerEventId)
            .set(BUSY_BLOCK.START_AT, newBlock.startAt)
            .set(BUSY_BLOCK.END_AT, newBlock.endAt)
            .set(BUSY_BLOCK.SUMMARY, newBlock.summary)
            .set(BUSY_BLOCK.CREATED_AT, now)
            .returning(
                BUSY_BLOCK.ID,
                BUSY_BLOCK.BUSINESS_ID,
                BUSY_BLOCK.STAFF_ID,
                BUSY_BLOCK.EXTERNAL_CALENDAR_ID,
                BUSY_BLOCK.PROVIDER_EVENT_ID,
                BUSY_BLOCK.START_AT,
                BUSY_BLOCK.END_AT,
                BUSY_BLOCK.SUMMARY,
                BUSY_BLOCK.CREATED_AT
            )
            .fetchOne() ?: error("Failed to insert busy block")

        return BusyBlock(
            id = record.get(BUSY_BLOCK.ID)!!,
            businessId = record.get(BUSY_BLOCK.BUSINESS_ID)!!,
            staffId = record.get(BUSY_BLOCK.STAFF_ID)!!,
            externalCalendarId = record.get(BUSY_BLOCK.EXTERNAL_CALENDAR_ID)!!,
            providerEventId = record.get(BUSY_BLOCK.PROVIDER_EVENT_ID)!!,
            startAt = record.get(BUSY_BLOCK.START_AT)!!,
            endAt = record.get(BUSY_BLOCK.END_AT)!!,
            summary = record.get(BUSY_BLOCK.SUMMARY),
            createdAt = record.get(BUSY_BLOCK.CREATED_AT)!!
        )
    }

    fun findById(id: UUID): BusyBlock? {
        val record = dsl.select(
            BUSY_BLOCK.ID,
            BUSY_BLOCK.BUSINESS_ID,
            BUSY_BLOCK.STAFF_ID,
            BUSY_BLOCK.EXTERNAL_CALENDAR_ID,
            BUSY_BLOCK.PROVIDER_EVENT_ID,
            BUSY_BLOCK.START_AT,
            BUSY_BLOCK.END_AT,
            BUSY_BLOCK.SUMMARY,
            BUSY_BLOCK.CREATED_AT
        )
            .from(BUSY_BLOCK)
            .where(BUSY_BLOCK.ID.eq(id))
            .fetchOne() ?: return null

        return BusyBlock(
            id = record.get(BUSY_BLOCK.ID)!!,
            businessId = record.get(BUSY_BLOCK.BUSINESS_ID)!!,
            staffId = record.get(BUSY_BLOCK.STAFF_ID)!!,
            externalCalendarId = record.get(BUSY_BLOCK.EXTERNAL_CALENDAR_ID)!!,
            providerEventId = record.get(BUSY_BLOCK.PROVIDER_EVENT_ID)!!,
            startAt = record.get(BUSY_BLOCK.START_AT)!!,
            endAt = record.get(BUSY_BLOCK.END_AT)!!,
            summary = record.get(BUSY_BLOCK.SUMMARY),
            createdAt = record.get(BUSY_BLOCK.CREATED_AT)!!
        )
    }

    fun update(id: UUID, update: BusyBlockUpdate): BusyBlock? {
        val record = dsl.update(BUSY_BLOCK)
            .set(BUSY_BLOCK.PROVIDER_EVENT_ID, update.providerEventId)
            .set(BUSY_BLOCK.START_AT, update.startAt)
            .set(BUSY_BLOCK.END_AT, update.endAt)
            .set(BUSY_BLOCK.SUMMARY, update.summary)
            .where(BUSY_BLOCK.ID.eq(id))
            .returning(
                BUSY_BLOCK.ID,
                BUSY_BLOCK.BUSINESS_ID,
                BUSY_BLOCK.STAFF_ID,
                BUSY_BLOCK.EXTERNAL_CALENDAR_ID,
                BUSY_BLOCK.PROVIDER_EVENT_ID,
                BUSY_BLOCK.START_AT,
                BUSY_BLOCK.END_AT,
                BUSY_BLOCK.SUMMARY,
                BUSY_BLOCK.CREATED_AT
            )
            .fetchOne() ?: return null

        return BusyBlock(
            id = record.get(BUSY_BLOCK.ID)!!,
            businessId = record.get(BUSY_BLOCK.BUSINESS_ID)!!,
            staffId = record.get(BUSY_BLOCK.STAFF_ID)!!,
            externalCalendarId = record.get(BUSY_BLOCK.EXTERNAL_CALENDAR_ID)!!,
            providerEventId = record.get(BUSY_BLOCK.PROVIDER_EVENT_ID)!!,
            startAt = record.get(BUSY_BLOCK.START_AT)!!,
            endAt = record.get(BUSY_BLOCK.END_AT)!!,
            summary = record.get(BUSY_BLOCK.SUMMARY),
            createdAt = record.get(BUSY_BLOCK.CREATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(BUSY_BLOCK)
            .where(BUSY_BLOCK.ID.eq(id))
            .execute() > 0
}
