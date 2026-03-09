package cz.lbenda.reservation.booking

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class BlockRepository(private val dsl: DSLContext) {
    fun create(newBlock: NewBlock): Block {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(BLOCK)
            .set(BLOCK.ID, newBlock.id)
            .set(BLOCK.BUSINESS_ID, newBlock.businessId)
            .set(BLOCK.LOCATION_ID, newBlock.locationId)
            .set(BLOCK.STAFF_ID, newBlock.staffId)
            .set(BLOCK.START_AT, newBlock.startAt)
            .set(BLOCK.END_AT, newBlock.endAt)
            .set(BLOCK.REASON, newBlock.reason)
            .set(BLOCK.CREATED_AT, now)
            .set(BLOCK.UPDATED_AT, now)
            .returning(
                BLOCK.ID,
                BLOCK.BUSINESS_ID,
                BLOCK.LOCATION_ID,
                BLOCK.STAFF_ID,
                BLOCK.START_AT,
                BLOCK.END_AT,
                BLOCK.REASON,
                BLOCK.CREATED_AT,
                BLOCK.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert block")

        return Block(
            id = record.get(BLOCK.ID)!!,
            businessId = record.get(BLOCK.BUSINESS_ID)!!,
            locationId = record.get(BLOCK.LOCATION_ID)!!,
            staffId = record.get(BLOCK.STAFF_ID),
            startAt = record.get(BLOCK.START_AT)!!,
            endAt = record.get(BLOCK.END_AT)!!,
            reason = record.get(BLOCK.REASON),
            createdAt = record.get(BLOCK.CREATED_AT)!!,
            updatedAt = record.get(BLOCK.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Block? {
        val record = dsl.select(
            BLOCK.ID,
            BLOCK.BUSINESS_ID,
            BLOCK.LOCATION_ID,
            BLOCK.STAFF_ID,
            BLOCK.START_AT,
            BLOCK.END_AT,
            BLOCK.REASON,
            BLOCK.CREATED_AT,
            BLOCK.UPDATED_AT
        )
            .from(BLOCK)
            .where(BLOCK.ID.eq(id))
            .fetchOne() ?: return null

        return Block(
            id = record.get(BLOCK.ID)!!,
            businessId = record.get(BLOCK.BUSINESS_ID)!!,
            locationId = record.get(BLOCK.LOCATION_ID)!!,
            staffId = record.get(BLOCK.STAFF_ID),
            startAt = record.get(BLOCK.START_AT)!!,
            endAt = record.get(BLOCK.END_AT)!!,
            reason = record.get(BLOCK.REASON),
            createdAt = record.get(BLOCK.CREATED_AT)!!,
            updatedAt = record.get(BLOCK.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: BlockUpdate): Block? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(BLOCK)
            .set(BLOCK.STAFF_ID, update.staffId)
            .set(BLOCK.START_AT, update.startAt)
            .set(BLOCK.END_AT, update.endAt)
            .set(BLOCK.REASON, update.reason)
            .set(BLOCK.UPDATED_AT, now)
            .where(BLOCK.ID.eq(id))
            .returning(
                BLOCK.ID,
                BLOCK.BUSINESS_ID,
                BLOCK.LOCATION_ID,
                BLOCK.STAFF_ID,
                BLOCK.START_AT,
                BLOCK.END_AT,
                BLOCK.REASON,
                BLOCK.CREATED_AT,
                BLOCK.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Block(
            id = record.get(BLOCK.ID)!!,
            businessId = record.get(BLOCK.BUSINESS_ID)!!,
            locationId = record.get(BLOCK.LOCATION_ID)!!,
            staffId = record.get(BLOCK.STAFF_ID),
            startAt = record.get(BLOCK.START_AT)!!,
            endAt = record.get(BLOCK.END_AT)!!,
            reason = record.get(BLOCK.REASON),
            createdAt = record.get(BLOCK.CREATED_AT)!!,
            updatedAt = record.get(BLOCK.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(BLOCK)
            .where(BLOCK.ID.eq(id))
            .execute() > 0
}
