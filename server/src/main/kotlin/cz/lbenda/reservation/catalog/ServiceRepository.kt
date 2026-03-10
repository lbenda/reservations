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
            .set(SERVICE.MIN_ADVANCE_MINUTES, newService.minAdvanceMinutes)
            .set(SERVICE.MAX_ADVANCE_DAYS, newService.maxAdvanceDays)
            .set(SERVICE.CANCELLATION_POLICY, newService.cancellationPolicy)
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
                SERVICE.MIN_ADVANCE_MINUTES,
                SERVICE.MAX_ADVANCE_DAYS,
                SERVICE.CANCELLATION_POLICY,
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
            minAdvanceMinutes = record.get(SERVICE.MIN_ADVANCE_MINUTES),
            maxAdvanceDays = record.get(SERVICE.MAX_ADVANCE_DAYS),
            cancellationPolicy = record.get(SERVICE.CANCELLATION_POLICY),
            priceAmount = record.get(SERVICE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(SERVICE.PRICE_CURRENCY)!!,
            isActive = record.get(SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(SERVICE.CREATED_AT)!!,
            updatedAt = record.get(SERVICE.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Service? {
        val record = selectBase()
            .from(SERVICE)
            .where(SERVICE.ID.eq(id))
            .fetchOne() ?: return null

        return toService(record)
    }

    fun findById(businessId: UUID, id: UUID): Service? {
        val record = selectBase()
            .from(SERVICE)
            .where(SERVICE.ID.eq(id).and(SERVICE.BUSINESS_ID.eq(businessId)))
            .fetchOne() ?: return null

        return toService(record)
    }

    fun listByBusiness(businessId: UUID, isActive: Boolean? = null): List<Service> {
        var condition: org.jooq.Condition = SERVICE.BUSINESS_ID.eq(businessId)
        if (isActive != null) {
            condition = condition.and(SERVICE.IS_ACTIVE.eq(isActive))
        }

        return selectBase()
            .from(SERVICE)
            .where(condition)
            .orderBy(SERVICE.NAME.asc())
            .fetch()
            .map { toService(it) }
    }

    fun update(id: UUID, update: ServiceUpdate): Service? =
        update(null, id, update)

    fun update(businessId: UUID?, id: UUID, update: ServiceUpdate): Service? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val updatedRecord = dsl.update(SERVICE)
            .set(SERVICE.SERVICE_CODE, update.serviceCode)
            .set(SERVICE.NAME, update.name)
            .set(SERVICE.DESCRIPTION, update.description)
            .set(SERVICE.DURATION_MINUTES, update.durationMinutes)
            .set(SERVICE.BUFFER_BEFORE_MINUTES, update.bufferBeforeMinutes)
            .set(SERVICE.BUFFER_AFTER_MINUTES, update.bufferAfterMinutes)
            .set(SERVICE.MIN_ADVANCE_MINUTES, update.minAdvanceMinutes)
            .set(SERVICE.MAX_ADVANCE_DAYS, update.maxAdvanceDays)
            .set(SERVICE.CANCELLATION_POLICY, update.cancellationPolicy)
            .set(SERVICE.PRICE_AMOUNT, update.priceAmount)
            .set(SERVICE.PRICE_CURRENCY, update.priceCurrency)
            .set(SERVICE.IS_ACTIVE, update.isActive)
            .set(SERVICE.UPDATED_AT, now)
            .where(
                if (businessId == null) {
                    SERVICE.ID.eq(id)
                } else {
                    SERVICE.ID.eq(id).and(SERVICE.BUSINESS_ID.eq(businessId))
                }
            )
            .returning(
                SERVICE.ID,
                SERVICE.BUSINESS_ID,
                SERVICE.SERVICE_CODE,
                SERVICE.NAME,
                SERVICE.DESCRIPTION,
                SERVICE.DURATION_MINUTES,
                SERVICE.BUFFER_BEFORE_MINUTES,
                SERVICE.BUFFER_AFTER_MINUTES,
                SERVICE.MIN_ADVANCE_MINUTES,
                SERVICE.MAX_ADVANCE_DAYS,
                SERVICE.CANCELLATION_POLICY,
                SERVICE.PRICE_AMOUNT,
                SERVICE.PRICE_CURRENCY,
                SERVICE.IS_ACTIVE,
                SERVICE.CREATED_AT,
                SERVICE.UPDATED_AT
            )
            .fetchOne() ?: return null

        return toService(updatedRecord)
    }

    fun archive(businessId: UUID, id: UUID): Service? {
        val existing = findById(businessId, id) ?: return null
        return update(
            businessId = businessId,
            id = id,
            update = with(existing) {
                ServiceUpdate(
                    serviceCode = serviceCode,
                    name = name,
                    description = description,
                    durationMinutes = durationMinutes,
                    bufferBeforeMinutes = bufferBeforeMinutes,
                    bufferAfterMinutes = bufferAfterMinutes,
                    minAdvanceMinutes = minAdvanceMinutes,
                    maxAdvanceDays = maxAdvanceDays,
                    cancellationPolicy = cancellationPolicy,
                    priceAmount = priceAmount,
                    priceCurrency = priceCurrency,
                    isActive = false
                )
            }
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(SERVICE)
            .where(SERVICE.ID.eq(id))
            .execute() > 0

    private fun selectBase() = dsl.select(
        SERVICE.ID,
        SERVICE.BUSINESS_ID,
        SERVICE.SERVICE_CODE,
        SERVICE.NAME,
        SERVICE.DESCRIPTION,
        SERVICE.DURATION_MINUTES,
        SERVICE.BUFFER_BEFORE_MINUTES,
        SERVICE.BUFFER_AFTER_MINUTES,
        SERVICE.MIN_ADVANCE_MINUTES,
        SERVICE.MAX_ADVANCE_DAYS,
        SERVICE.CANCELLATION_POLICY,
        SERVICE.PRICE_AMOUNT,
        SERVICE.PRICE_CURRENCY,
        SERVICE.IS_ACTIVE,
        SERVICE.CREATED_AT,
        SERVICE.UPDATED_AT
    )

    private fun toService(record: org.jooq.Record): Service =
        Service(
            id = record.get(SERVICE.ID)!!,
            businessId = record.get(SERVICE.BUSINESS_ID)!!,
            serviceCode = record.get(SERVICE.SERVICE_CODE),
            name = record.get(SERVICE.NAME)!!,
            description = record.get(SERVICE.DESCRIPTION),
            durationMinutes = record.get(SERVICE.DURATION_MINUTES)!!,
            bufferBeforeMinutes = record.get(SERVICE.BUFFER_BEFORE_MINUTES),
            bufferAfterMinutes = record.get(SERVICE.BUFFER_AFTER_MINUTES),
            minAdvanceMinutes = record.get(SERVICE.MIN_ADVANCE_MINUTES),
            maxAdvanceDays = record.get(SERVICE.MAX_ADVANCE_DAYS),
            cancellationPolicy = record.get(SERVICE.CANCELLATION_POLICY),
            priceAmount = record.get(SERVICE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(SERVICE.PRICE_CURRENCY)!!,
            isActive = record.get(SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(SERVICE.CREATED_AT)!!,
            updatedAt = record.get(SERVICE.UPDATED_AT)!!
        )
}
