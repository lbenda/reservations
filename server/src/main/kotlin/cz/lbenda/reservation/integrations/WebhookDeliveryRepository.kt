package cz.lbenda.reservation.integrations

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.util.UUID

class WebhookDeliveryRepository(private val dsl: DSLContext) {
    fun create(newDelivery: NewWebhookDelivery): WebhookDelivery {
        val record = dsl.insertInto(WEBHOOK_DELIVERY)
            .set(WEBHOOK_DELIVERY.ID, newDelivery.id)
            .set(WEBHOOK_DELIVERY.BUSINESS_ID, newDelivery.businessId)
            .set(WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID, newDelivery.webhookEndpointId)
            .set(WEBHOOK_DELIVERY.EVENT_ID, newDelivery.eventId)
            .set(WEBHOOK_DELIVERY.DELIVERY_KEY, newDelivery.deliveryKey)
            .set(WEBHOOK_DELIVERY.STATUS, newDelivery.status)
            .set(WEBHOOK_DELIVERY.ATTEMPT_COUNT, newDelivery.attemptCount)
            .set(WEBHOOK_DELIVERY.LAST_ATTEMPT_AT, newDelivery.lastAttemptAt)
            .set(WEBHOOK_DELIVERY.RESPONSE_CODE, newDelivery.responseCode)
            .set(WEBHOOK_DELIVERY.CREATED_AT, java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC))
            .returning(
                WEBHOOK_DELIVERY.ID,
                WEBHOOK_DELIVERY.BUSINESS_ID,
                WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID,
                WEBHOOK_DELIVERY.EVENT_ID,
                WEBHOOK_DELIVERY.DELIVERY_KEY,
                WEBHOOK_DELIVERY.STATUS,
                WEBHOOK_DELIVERY.ATTEMPT_COUNT,
                WEBHOOK_DELIVERY.LAST_ATTEMPT_AT,
                WEBHOOK_DELIVERY.RESPONSE_CODE,
                WEBHOOK_DELIVERY.CREATED_AT
            )
            .fetchOne() ?: error("Failed to insert webhook delivery")

        return WebhookDelivery(
            id = record.get(WEBHOOK_DELIVERY.ID)!!,
            businessId = record.get(WEBHOOK_DELIVERY.BUSINESS_ID)!!,
            webhookEndpointId = record.get(WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID)!!,
            eventId = record.get(WEBHOOK_DELIVERY.EVENT_ID)!!,
            deliveryKey = record.get(WEBHOOK_DELIVERY.DELIVERY_KEY)!!,
            status = record.get(WEBHOOK_DELIVERY.STATUS)!!,
            attemptCount = record.get(WEBHOOK_DELIVERY.ATTEMPT_COUNT)!!,
            lastAttemptAt = record.get(WEBHOOK_DELIVERY.LAST_ATTEMPT_AT),
            responseCode = record.get(WEBHOOK_DELIVERY.RESPONSE_CODE),
            createdAt = record.get(WEBHOOK_DELIVERY.CREATED_AT)!!
        )
    }

    fun findById(id: UUID): WebhookDelivery? {
        val record = dsl.select(
            WEBHOOK_DELIVERY.ID,
            WEBHOOK_DELIVERY.BUSINESS_ID,
            WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID,
            WEBHOOK_DELIVERY.EVENT_ID,
            WEBHOOK_DELIVERY.DELIVERY_KEY,
            WEBHOOK_DELIVERY.STATUS,
            WEBHOOK_DELIVERY.ATTEMPT_COUNT,
            WEBHOOK_DELIVERY.LAST_ATTEMPT_AT,
            WEBHOOK_DELIVERY.RESPONSE_CODE,
            WEBHOOK_DELIVERY.CREATED_AT
        )
            .from(WEBHOOK_DELIVERY)
            .where(WEBHOOK_DELIVERY.ID.eq(id))
            .fetchOne() ?: return null

        return WebhookDelivery(
            id = record.get(WEBHOOK_DELIVERY.ID)!!,
            businessId = record.get(WEBHOOK_DELIVERY.BUSINESS_ID)!!,
            webhookEndpointId = record.get(WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID)!!,
            eventId = record.get(WEBHOOK_DELIVERY.EVENT_ID)!!,
            deliveryKey = record.get(WEBHOOK_DELIVERY.DELIVERY_KEY)!!,
            status = record.get(WEBHOOK_DELIVERY.STATUS)!!,
            attemptCount = record.get(WEBHOOK_DELIVERY.ATTEMPT_COUNT)!!,
            lastAttemptAt = record.get(WEBHOOK_DELIVERY.LAST_ATTEMPT_AT),
            responseCode = record.get(WEBHOOK_DELIVERY.RESPONSE_CODE),
            createdAt = record.get(WEBHOOK_DELIVERY.CREATED_AT)!!
        )
    }

    fun update(id: UUID, update: WebhookDeliveryUpdate): WebhookDelivery? {
        val record = dsl.update(WEBHOOK_DELIVERY)
            .set(WEBHOOK_DELIVERY.STATUS, update.status)
            .set(WEBHOOK_DELIVERY.ATTEMPT_COUNT, update.attemptCount)
            .set(WEBHOOK_DELIVERY.LAST_ATTEMPT_AT, update.lastAttemptAt)
            .set(WEBHOOK_DELIVERY.RESPONSE_CODE, update.responseCode)
            .where(WEBHOOK_DELIVERY.ID.eq(id))
            .returning(
                WEBHOOK_DELIVERY.ID,
                WEBHOOK_DELIVERY.BUSINESS_ID,
                WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID,
                WEBHOOK_DELIVERY.EVENT_ID,
                WEBHOOK_DELIVERY.DELIVERY_KEY,
                WEBHOOK_DELIVERY.STATUS,
                WEBHOOK_DELIVERY.ATTEMPT_COUNT,
                WEBHOOK_DELIVERY.LAST_ATTEMPT_AT,
                WEBHOOK_DELIVERY.RESPONSE_CODE,
                WEBHOOK_DELIVERY.CREATED_AT
            )
            .fetchOne() ?: return null

        return WebhookDelivery(
            id = record.get(WEBHOOK_DELIVERY.ID)!!,
            businessId = record.get(WEBHOOK_DELIVERY.BUSINESS_ID)!!,
            webhookEndpointId = record.get(WEBHOOK_DELIVERY.WEBHOOK_ENDPOINT_ID)!!,
            eventId = record.get(WEBHOOK_DELIVERY.EVENT_ID)!!,
            deliveryKey = record.get(WEBHOOK_DELIVERY.DELIVERY_KEY)!!,
            status = record.get(WEBHOOK_DELIVERY.STATUS)!!,
            attemptCount = record.get(WEBHOOK_DELIVERY.ATTEMPT_COUNT)!!,
            lastAttemptAt = record.get(WEBHOOK_DELIVERY.LAST_ATTEMPT_AT),
            responseCode = record.get(WEBHOOK_DELIVERY.RESPONSE_CODE),
            createdAt = record.get(WEBHOOK_DELIVERY.CREATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(WEBHOOK_DELIVERY)
            .where(WEBHOOK_DELIVERY.ID.eq(id))
            .execute() > 0
}
