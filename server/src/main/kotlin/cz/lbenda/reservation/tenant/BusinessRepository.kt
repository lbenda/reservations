package cz.lbenda.reservation.tenant

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class BusinessRepository(private val dsl: DSLContext) {
    fun create(newBusiness: NewBusiness): Business {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(BUSINESS)
            .set(BUSINESS.ID, newBusiness.id)
            .set(BUSINESS.SLUG, newBusiness.slug)
            .set(BUSINESS.NAME, newBusiness.name)
            .set(BUSINESS.TIMEZONE, newBusiness.timezone)
            .set(BUSINESS.CURRENCY, newBusiness.currency)
            .set(BUSINESS.STATUS, newBusiness.status)
            .set(BUSINESS.CREATED_AT, now)
            .set(BUSINESS.UPDATED_AT, now)
            .returning(
                BUSINESS.ID,
                BUSINESS.SLUG,
                BUSINESS.NAME,
                BUSINESS.TIMEZONE,
                BUSINESS.CURRENCY,
                BUSINESS.STATUS,
                BUSINESS.CREATED_AT,
                BUSINESS.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert business")

        return Business(
            id = record.get(BUSINESS.ID)!!,
            slug = record.get(BUSINESS.SLUG)!!,
            name = record.get(BUSINESS.NAME)!!,
            timezone = record.get(BUSINESS.TIMEZONE)!!,
            currency = record.get(BUSINESS.CURRENCY)!!,
            status = record.get(BUSINESS.STATUS)!!,
            createdAt = record.get(BUSINESS.CREATED_AT)!!,
            updatedAt = record.get(BUSINESS.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Business? {
        val record = dsl.select(
            BUSINESS.ID,
            BUSINESS.SLUG,
            BUSINESS.NAME,
            BUSINESS.TIMEZONE,
            BUSINESS.CURRENCY,
            BUSINESS.STATUS,
            BUSINESS.CREATED_AT,
            BUSINESS.UPDATED_AT
        )
            .from(BUSINESS)
            .where(BUSINESS.ID.eq(id))
            .fetchOne() ?: return null

        return Business(
            id = record.get(BUSINESS.ID)!!,
            slug = record.get(BUSINESS.SLUG)!!,
            name = record.get(BUSINESS.NAME)!!,
            timezone = record.get(BUSINESS.TIMEZONE)!!,
            currency = record.get(BUSINESS.CURRENCY)!!,
            status = record.get(BUSINESS.STATUS)!!,
            createdAt = record.get(BUSINESS.CREATED_AT)!!,
            updatedAt = record.get(BUSINESS.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: BusinessUpdate): Business? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(BUSINESS)
            .set(BUSINESS.SLUG, update.slug)
            .set(BUSINESS.NAME, update.name)
            .set(BUSINESS.TIMEZONE, update.timezone)
            .set(BUSINESS.CURRENCY, update.currency)
            .set(BUSINESS.STATUS, update.status)
            .set(BUSINESS.UPDATED_AT, now)
            .where(BUSINESS.ID.eq(id))
            .returning(
                BUSINESS.ID,
                BUSINESS.SLUG,
                BUSINESS.NAME,
                BUSINESS.TIMEZONE,
                BUSINESS.CURRENCY,
                BUSINESS.STATUS,
                BUSINESS.CREATED_AT,
                BUSINESS.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Business(
            id = record.get(BUSINESS.ID)!!,
            slug = record.get(BUSINESS.SLUG)!!,
            name = record.get(BUSINESS.NAME)!!,
            timezone = record.get(BUSINESS.TIMEZONE)!!,
            currency = record.get(BUSINESS.CURRENCY)!!,
            status = record.get(BUSINESS.STATUS)!!,
            createdAt = record.get(BUSINESS.CREATED_AT)!!,
            updatedAt = record.get(BUSINESS.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(BUSINESS)
            .where(BUSINESS.ID.eq(id))
            .execute() > 0
}
