package cz.lbenda.reservation.catalog

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ServiceRepository(private val dsl: DSLContext) {
    fun create(newService: NewService): Service {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(SERVICE)
            .set(SERVICE.ID, newService.id)
            .set(SERVICE.BUSINESS_ID, newService.businessId)
            .set(SERVICE.SERVICE_CODE, newService.serviceCode)
            .set(SERVICE.NAME, newService.name)
            .set(SERVICE.DESCRIPTION, newService.description)
            .set(SERVICE.DURATION_MINUTES, newService.durationMinutes)
            .set(SERVICE.BUFFER_BEFORE_MINUTES, newService.bufferBeforeMinutes)
            .set(SERVICE.BUFFER_AFTER_MINUTES, newService.bufferAfterMinutes)
            .set(SERVICE.PRICE_AMOUNT, newService.priceAmount)
            .set(SERVICE.PRICE_CURRENCY, newService.priceCurrency)
            .set(SERVICE.IS_ACTIVE, newService.isActive)
            .set(SERVICE.CREATED_AT, now)
            .set(SERVICE.UPDATED_AT, now)
            .returning(
                SERVICE.ID,
                SERVICE.BUSINESS_ID,
                SERVICE.SERVICE_CODE,
                SERVICE.NAME,
                SERVICE.DESCRIPTION,
                SERVICE.DURATION_MINUTES,
                SERVICE.BUFFER_BEFORE_MINUTES,
                SERVICE.BUFFER_AFTER_MINUTES,
                SERVICE.PRICE_AMOUNT,
                SERVICE.PRICE_CURRENCY,
                SERVICE.IS_ACTIVE,
                SERVICE.CREATED_AT,
                SERVICE.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert service")

        return Service(
            id = record.get(SERVICE.ID)!!,
            businessId = record.get(SERVICE.BUSINESS_ID)!!,
            serviceCode = record.get(SERVICE.SERVICE_CODE),
            name = record.get(SERVICE.NAME)!!,
            description = record.get(SERVICE.DESCRIPTION),
            durationMinutes = record.get(SERVICE.DURATION_MINUTES)!!,
            bufferBeforeMinutes = record.get(SERVICE.BUFFER_BEFORE_MINUTES),
            bufferAfterMinutes = record.get(SERVICE.BUFFER_AFTER_MINUTES),
            priceAmount = record.get(SERVICE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(SERVICE.PRICE_CURRENCY)!!,
            isActive = record.get(SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(SERVICE.CREATED_AT)!!,
            updatedAt = record.get(SERVICE.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Service? {
        val record = dsl.select(
            SERVICE.ID,
            SERVICE.BUSINESS_ID,
            SERVICE.SERVICE_CODE,
            SERVICE.NAME,
            SERVICE.DESCRIPTION,
            SERVICE.DURATION_MINUTES,
            SERVICE.BUFFER_BEFORE_MINUTES,
            SERVICE.BUFFER_AFTER_MINUTES,
            SERVICE.PRICE_AMOUNT,
            SERVICE.PRICE_CURRENCY,
            SERVICE.IS_ACTIVE,
            SERVICE.CREATED_AT,
            SERVICE.UPDATED_AT
        )
            .from(SERVICE)
            .where(SERVICE.ID.eq(id))
            .fetchOne() ?: return null

        return Service(
            id = record.get(SERVICE.ID)!!,
            businessId = record.get(SERVICE.BUSINESS_ID)!!,
            serviceCode = record.get(SERVICE.SERVICE_CODE),
            name = record.get(SERVICE.NAME)!!,
            description = record.get(SERVICE.DESCRIPTION),
            durationMinutes = record.get(SERVICE.DURATION_MINUTES)!!,
            bufferBeforeMinutes = record.get(SERVICE.BUFFER_BEFORE_MINUTES),
            bufferAfterMinutes = record.get(SERVICE.BUFFER_AFTER_MINUTES),
            priceAmount = record.get(SERVICE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(SERVICE.PRICE_CURRENCY)!!,
            isActive = record.get(SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(SERVICE.CREATED_AT)!!,
            updatedAt = record.get(SERVICE.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: ServiceUpdate): Service? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(SERVICE)
            .set(SERVICE.SERVICE_CODE, update.serviceCode)
            .set(SERVICE.NAME, update.name)
            .set(SERVICE.DESCRIPTION, update.description)
            .set(SERVICE.DURATION_MINUTES, update.durationMinutes)
            .set(SERVICE.BUFFER_BEFORE_MINUTES, update.bufferBeforeMinutes)
            .set(SERVICE.BUFFER_AFTER_MINUTES, update.bufferAfterMinutes)
            .set(SERVICE.PRICE_AMOUNT, update.priceAmount)
            .set(SERVICE.PRICE_CURRENCY, update.priceCurrency)
            .set(SERVICE.IS_ACTIVE, update.isActive)
            .set(SERVICE.UPDATED_AT, now)
            .where(SERVICE.ID.eq(id))
            .returning(
                SERVICE.ID,
                SERVICE.BUSINESS_ID,
                SERVICE.SERVICE_CODE,
                SERVICE.NAME,
                SERVICE.DESCRIPTION,
                SERVICE.DURATION_MINUTES,
                SERVICE.BUFFER_BEFORE_MINUTES,
                SERVICE.BUFFER_AFTER_MINUTES,
                SERVICE.PRICE_AMOUNT,
                SERVICE.PRICE_CURRENCY,
                SERVICE.IS_ACTIVE,
                SERVICE.CREATED_AT,
                SERVICE.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Service(
            id = record.get(SERVICE.ID)!!,
            businessId = record.get(SERVICE.BUSINESS_ID)!!,
            serviceCode = record.get(SERVICE.SERVICE_CODE),
            name = record.get(SERVICE.NAME)!!,
            description = record.get(SERVICE.DESCRIPTION),
            durationMinutes = record.get(SERVICE.DURATION_MINUTES)!!,
            bufferBeforeMinutes = record.get(SERVICE.BUFFER_BEFORE_MINUTES),
            bufferAfterMinutes = record.get(SERVICE.BUFFER_AFTER_MINUTES),
            priceAmount = record.get(SERVICE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(SERVICE.PRICE_CURRENCY)!!,
            isActive = record.get(SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(SERVICE.CREATED_AT)!!,
            updatedAt = record.get(SERVICE.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(SERVICE)
            .where(SERVICE.ID.eq(id))
            .execute() > 0
}
