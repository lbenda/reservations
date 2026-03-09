package cz.lbenda.reservation.integrations

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class WebhookEndpointRepository(private val dsl: DSLContext) {
    fun create(newEndpoint: NewWebhookEndpoint): WebhookEndpoint {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(WEBHOOK_ENDPOINT)
            .set(WEBHOOK_ENDPOINT.ID, newEndpoint.id)
            .set(WEBHOOK_ENDPOINT.BUSINESS_ID, newEndpoint.businessId)
            .set(WEBHOOK_ENDPOINT.ENDPOINT_KEY, newEndpoint.endpointKey)
            .set(WEBHOOK_ENDPOINT.URL, newEndpoint.url)
            .set(WEBHOOK_ENDPOINT.IS_ACTIVE, newEndpoint.isActive)
            .set(WEBHOOK_ENDPOINT.SECRET_REF, newEndpoint.secretRef)
            .set(WEBHOOK_ENDPOINT.CREATED_AT, now)
            .set(WEBHOOK_ENDPOINT.UPDATED_AT, now)
            .returning(
                WEBHOOK_ENDPOINT.ID,
                WEBHOOK_ENDPOINT.BUSINESS_ID,
                WEBHOOK_ENDPOINT.ENDPOINT_KEY,
                WEBHOOK_ENDPOINT.URL,
                WEBHOOK_ENDPOINT.IS_ACTIVE,
                WEBHOOK_ENDPOINT.SECRET_REF,
                WEBHOOK_ENDPOINT.CREATED_AT,
                WEBHOOK_ENDPOINT.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert webhook endpoint")

        return WebhookEndpoint(
            id = record.get(WEBHOOK_ENDPOINT.ID)!!,
            businessId = record.get(WEBHOOK_ENDPOINT.BUSINESS_ID)!!,
            endpointKey = record.get(WEBHOOK_ENDPOINT.ENDPOINT_KEY)!!,
            url = record.get(WEBHOOK_ENDPOINT.URL)!!,
            isActive = record.get(WEBHOOK_ENDPOINT.IS_ACTIVE)!!,
            secretRef = record.get(WEBHOOK_ENDPOINT.SECRET_REF),
            createdAt = record.get(WEBHOOK_ENDPOINT.CREATED_AT)!!,
            updatedAt = record.get(WEBHOOK_ENDPOINT.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): WebhookEndpoint? {
        val record = dsl.select(
            WEBHOOK_ENDPOINT.ID,
            WEBHOOK_ENDPOINT.BUSINESS_ID,
            WEBHOOK_ENDPOINT.ENDPOINT_KEY,
            WEBHOOK_ENDPOINT.URL,
            WEBHOOK_ENDPOINT.IS_ACTIVE,
            WEBHOOK_ENDPOINT.SECRET_REF,
            WEBHOOK_ENDPOINT.CREATED_AT,
            WEBHOOK_ENDPOINT.UPDATED_AT
        )
            .from(WEBHOOK_ENDPOINT)
            .where(WEBHOOK_ENDPOINT.ID.eq(id))
            .fetchOne() ?: return null

        return WebhookEndpoint(
            id = record.get(WEBHOOK_ENDPOINT.ID)!!,
            businessId = record.get(WEBHOOK_ENDPOINT.BUSINESS_ID)!!,
            endpointKey = record.get(WEBHOOK_ENDPOINT.ENDPOINT_KEY)!!,
            url = record.get(WEBHOOK_ENDPOINT.URL)!!,
            isActive = record.get(WEBHOOK_ENDPOINT.IS_ACTIVE)!!,
            secretRef = record.get(WEBHOOK_ENDPOINT.SECRET_REF),
            createdAt = record.get(WEBHOOK_ENDPOINT.CREATED_AT)!!,
            updatedAt = record.get(WEBHOOK_ENDPOINT.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: WebhookEndpointUpdate): WebhookEndpoint? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(WEBHOOK_ENDPOINT)
            .set(WEBHOOK_ENDPOINT.URL, update.url)
            .set(WEBHOOK_ENDPOINT.IS_ACTIVE, update.isActive)
            .set(WEBHOOK_ENDPOINT.SECRET_REF, update.secretRef)
            .set(WEBHOOK_ENDPOINT.UPDATED_AT, now)
            .where(WEBHOOK_ENDPOINT.ID.eq(id))
            .returning(
                WEBHOOK_ENDPOINT.ID,
                WEBHOOK_ENDPOINT.BUSINESS_ID,
                WEBHOOK_ENDPOINT.ENDPOINT_KEY,
                WEBHOOK_ENDPOINT.URL,
                WEBHOOK_ENDPOINT.IS_ACTIVE,
                WEBHOOK_ENDPOINT.SECRET_REF,
                WEBHOOK_ENDPOINT.CREATED_AT,
                WEBHOOK_ENDPOINT.UPDATED_AT
            )
            .fetchOne() ?: return null

        return WebhookEndpoint(
            id = record.get(WEBHOOK_ENDPOINT.ID)!!,
            businessId = record.get(WEBHOOK_ENDPOINT.BUSINESS_ID)!!,
            endpointKey = record.get(WEBHOOK_ENDPOINT.ENDPOINT_KEY)!!,
            url = record.get(WEBHOOK_ENDPOINT.URL)!!,
            isActive = record.get(WEBHOOK_ENDPOINT.IS_ACTIVE)!!,
            secretRef = record.get(WEBHOOK_ENDPOINT.SECRET_REF),
            createdAt = record.get(WEBHOOK_ENDPOINT.CREATED_AT)!!,
            updatedAt = record.get(WEBHOOK_ENDPOINT.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(WEBHOOK_ENDPOINT)
            .where(WEBHOOK_ENDPOINT.ID.eq(id))
            .execute() > 0
}
