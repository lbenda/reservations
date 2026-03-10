package cz.lbenda.reservation.tenant

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ApiKeyRepository(private val dsl: DSLContext) {
    fun create(newApiKey: NewApiKey): ApiKey {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(API_KEY)
            .set(API_KEY.ID, newApiKey.id)
            .set(API_KEY.BUSINESS_ID, newApiKey.businessId)
            .set(API_KEY.KEY_ID, newApiKey.keyId)
            .set(API_KEY.NAME, newApiKey.name)
            .set(API_KEY.CREATED_AT, now)
            .returning(
                API_KEY.ID,
                API_KEY.BUSINESS_ID,
                API_KEY.KEY_ID,
                API_KEY.NAME,
                API_KEY.LAST_USED_AT,
                API_KEY.REVOKED_AT,
                API_KEY.CREATED_AT
            )
            .fetchOne() ?: error("Failed to insert api key")

        return ApiKey(
            id = record.get(API_KEY.ID)!!,
            businessId = record.get(API_KEY.BUSINESS_ID)!!,
            keyId = record.get(API_KEY.KEY_ID)!!,
            name = record.get(API_KEY.NAME)!!,
            lastUsedAt = record.get(API_KEY.LAST_USED_AT),
            revokedAt = record.get(API_KEY.REVOKED_AT),
            createdAt = record.get(API_KEY.CREATED_AT)!!
        )
    }

    fun findById(id: UUID): ApiKey? {
        val record = dsl.select(
            API_KEY.ID,
            API_KEY.BUSINESS_ID,
            API_KEY.KEY_ID,
            API_KEY.NAME,
            API_KEY.LAST_USED_AT,
            API_KEY.REVOKED_AT,
            API_KEY.CREATED_AT
        )
            .from(API_KEY)
            .where(API_KEY.ID.eq(id))
            .fetchOne() ?: return null

        return ApiKey(
            id = record.get(API_KEY.ID)!!,
            businessId = record.get(API_KEY.BUSINESS_ID)!!,
            keyId = record.get(API_KEY.KEY_ID)!!,
            name = record.get(API_KEY.NAME)!!,
            lastUsedAt = record.get(API_KEY.LAST_USED_AT),
            revokedAt = record.get(API_KEY.REVOKED_AT),
            createdAt = record.get(API_KEY.CREATED_AT)!!
        )
    }

    fun update(id: UUID, update: ApiKeyUpdate): ApiKey? {
        val record = dsl.update(API_KEY)
            .set(API_KEY.NAME, update.name)
            .set(API_KEY.LAST_USED_AT, update.lastUsedAt)
            .set(API_KEY.REVOKED_AT, update.revokedAt)
            .where(API_KEY.ID.eq(id))
            .returning(
                API_KEY.ID,
                API_KEY.BUSINESS_ID,
                API_KEY.KEY_ID,
                API_KEY.NAME,
                API_KEY.LAST_USED_AT,
                API_KEY.REVOKED_AT,
                API_KEY.CREATED_AT
            )
            .fetchOne() ?: return null

        return ApiKey(
            id = record.get(API_KEY.ID)!!,
            businessId = record.get(API_KEY.BUSINESS_ID)!!,
            keyId = record.get(API_KEY.KEY_ID)!!,
            name = record.get(API_KEY.NAME)!!,
            lastUsedAt = record.get(API_KEY.LAST_USED_AT),
            revokedAt = record.get(API_KEY.REVOKED_AT),
            createdAt = record.get(API_KEY.CREATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(API_KEY)
            .where(API_KEY.ID.eq(id))
            .execute() > 0
}
