package cz.lbenda.reservation.tenant

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class BusinessUserRepository(private val dsl: DSLContext) {
    fun create(newBusinessUser: NewBusinessUser): BusinessUser {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(BUSINESS_USER)
            .set(BUSINESS_USER.ID, newBusinessUser.id)
            .set(BUSINESS_USER.BUSINESS_ID, newBusinessUser.businessId)
            .set(BUSINESS_USER.USER_ID, newBusinessUser.userId)
            .set(BUSINESS_USER.ROLE_ID, newBusinessUser.roleId)
            .set(BUSINESS_USER.BUSINESS_USER_KEY, newBusinessUser.businessUserKey)
            .set(BUSINESS_USER.STATUS, newBusinessUser.status)
            .set(BUSINESS_USER.CREATED_AT, now)
            .set(BUSINESS_USER.UPDATED_AT, now)
            .returning(
                BUSINESS_USER.ID,
                BUSINESS_USER.BUSINESS_ID,
                BUSINESS_USER.USER_ID,
                BUSINESS_USER.ROLE_ID,
                BUSINESS_USER.BUSINESS_USER_KEY,
                BUSINESS_USER.STATUS,
                BUSINESS_USER.CREATED_AT,
                BUSINESS_USER.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert business user")

        return BusinessUser(
            id = record.get(BUSINESS_USER.ID)!!,
            businessId = record.get(BUSINESS_USER.BUSINESS_ID)!!,
            userId = record.get(BUSINESS_USER.USER_ID)!!,
            roleId = record.get(BUSINESS_USER.ROLE_ID)!!,
            businessUserKey = record.get(BUSINESS_USER.BUSINESS_USER_KEY),
            status = record.get(BUSINESS_USER.STATUS)!!,
            createdAt = record.get(BUSINESS_USER.CREATED_AT)!!,
            updatedAt = record.get(BUSINESS_USER.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): BusinessUser? {
        val record = dsl.select(
            BUSINESS_USER.ID,
            BUSINESS_USER.BUSINESS_ID,
            BUSINESS_USER.USER_ID,
            BUSINESS_USER.ROLE_ID,
            BUSINESS_USER.BUSINESS_USER_KEY,
            BUSINESS_USER.STATUS,
            BUSINESS_USER.CREATED_AT,
            BUSINESS_USER.UPDATED_AT
        )
            .from(BUSINESS_USER)
            .where(BUSINESS_USER.ID.eq(id))
            .fetchOne() ?: return null

        return BusinessUser(
            id = record.get(BUSINESS_USER.ID)!!,
            businessId = record.get(BUSINESS_USER.BUSINESS_ID)!!,
            userId = record.get(BUSINESS_USER.USER_ID)!!,
            roleId = record.get(BUSINESS_USER.ROLE_ID)!!,
            businessUserKey = record.get(BUSINESS_USER.BUSINESS_USER_KEY),
            status = record.get(BUSINESS_USER.STATUS)!!,
            createdAt = record.get(BUSINESS_USER.CREATED_AT)!!,
            updatedAt = record.get(BUSINESS_USER.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: BusinessUserUpdate): BusinessUser? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(BUSINESS_USER)
            .set(BUSINESS_USER.ROLE_ID, update.roleId)
            .set(BUSINESS_USER.BUSINESS_USER_KEY, update.businessUserKey)
            .set(BUSINESS_USER.STATUS, update.status)
            .set(BUSINESS_USER.UPDATED_AT, now)
            .where(BUSINESS_USER.ID.eq(id))
            .returning(
                BUSINESS_USER.ID,
                BUSINESS_USER.BUSINESS_ID,
                BUSINESS_USER.USER_ID,
                BUSINESS_USER.ROLE_ID,
                BUSINESS_USER.BUSINESS_USER_KEY,
                BUSINESS_USER.STATUS,
                BUSINESS_USER.CREATED_AT,
                BUSINESS_USER.UPDATED_AT
            )
            .fetchOne() ?: return null

        return BusinessUser(
            id = record.get(BUSINESS_USER.ID)!!,
            businessId = record.get(BUSINESS_USER.BUSINESS_ID)!!,
            userId = record.get(BUSINESS_USER.USER_ID)!!,
            roleId = record.get(BUSINESS_USER.ROLE_ID)!!,
            businessUserKey = record.get(BUSINESS_USER.BUSINESS_USER_KEY),
            status = record.get(BUSINESS_USER.STATUS)!!,
            createdAt = record.get(BUSINESS_USER.CREATED_AT)!!,
            updatedAt = record.get(BUSINESS_USER.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(BUSINESS_USER)
            .where(BUSINESS_USER.ID.eq(id))
            .execute() > 0
}
