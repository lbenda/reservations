package cz.lbenda.reservation.tenant

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class UserRepository(private val dsl: DSLContext) {
    fun create(newUser: NewUser): User {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(APP_USER)
            .set(APP_USER.ID, newUser.id)
            .set(APP_USER.EMAIL, newUser.email)
            .set(APP_USER.FIRST_NAME, newUser.firstName)
            .set(APP_USER.LAST_NAME, newUser.lastName)
            .set(APP_USER.PHONE, newUser.phone)
            .set(APP_USER.LOCALE, newUser.locale)
            .set(APP_USER.STATUS, newUser.status)
            .set(APP_USER.CREATED_AT, now)
            .set(APP_USER.UPDATED_AT, now)
            .returning(
                APP_USER.ID,
                APP_USER.EMAIL,
                APP_USER.FIRST_NAME,
                APP_USER.LAST_NAME,
                APP_USER.PHONE,
                APP_USER.LOCALE,
                APP_USER.STATUS,
                APP_USER.LAST_LOGIN_AT,
                APP_USER.CREATED_AT,
                APP_USER.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert user")

        return User(
            id = record.get(APP_USER.ID)!!,
            email = record.get(APP_USER.EMAIL)!!,
            firstName = record.get(APP_USER.FIRST_NAME)!!,
            lastName = record.get(APP_USER.LAST_NAME)!!,
            phone = record.get(APP_USER.PHONE),
            locale = record.get(APP_USER.LOCALE),
            status = record.get(APP_USER.STATUS)!!,
            lastLoginAt = record.get(APP_USER.LAST_LOGIN_AT),
            createdAt = record.get(APP_USER.CREATED_AT)!!,
            updatedAt = record.get(APP_USER.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): User? {
        val record = dsl.select(
            APP_USER.ID,
            APP_USER.EMAIL,
            APP_USER.FIRST_NAME,
            APP_USER.LAST_NAME,
            APP_USER.PHONE,
            APP_USER.LOCALE,
            APP_USER.STATUS,
            APP_USER.LAST_LOGIN_AT,
            APP_USER.CREATED_AT,
            APP_USER.UPDATED_AT
        )
            .from(APP_USER)
            .where(APP_USER.ID.eq(id))
            .fetchOne() ?: return null

        return User(
            id = record.get(APP_USER.ID)!!,
            email = record.get(APP_USER.EMAIL)!!,
            firstName = record.get(APP_USER.FIRST_NAME)!!,
            lastName = record.get(APP_USER.LAST_NAME)!!,
            phone = record.get(APP_USER.PHONE),
            locale = record.get(APP_USER.LOCALE),
            status = record.get(APP_USER.STATUS)!!,
            lastLoginAt = record.get(APP_USER.LAST_LOGIN_AT),
            createdAt = record.get(APP_USER.CREATED_AT)!!,
            updatedAt = record.get(APP_USER.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: UserUpdate): User? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(APP_USER)
            .set(APP_USER.EMAIL, update.email)
            .set(APP_USER.FIRST_NAME, update.firstName)
            .set(APP_USER.LAST_NAME, update.lastName)
            .set(APP_USER.PHONE, update.phone)
            .set(APP_USER.LOCALE, update.locale)
            .set(APP_USER.STATUS, update.status)
            .set(APP_USER.LAST_LOGIN_AT, update.lastLoginAt)
            .set(APP_USER.UPDATED_AT, now)
            .where(APP_USER.ID.eq(id))
            .returning(
                APP_USER.ID,
                APP_USER.EMAIL,
                APP_USER.FIRST_NAME,
                APP_USER.LAST_NAME,
                APP_USER.PHONE,
                APP_USER.LOCALE,
                APP_USER.STATUS,
                APP_USER.LAST_LOGIN_AT,
                APP_USER.CREATED_AT,
                APP_USER.UPDATED_AT
            )
            .fetchOne() ?: return null

        return User(
            id = record.get(APP_USER.ID)!!,
            email = record.get(APP_USER.EMAIL)!!,
            firstName = record.get(APP_USER.FIRST_NAME)!!,
            lastName = record.get(APP_USER.LAST_NAME)!!,
            phone = record.get(APP_USER.PHONE),
            locale = record.get(APP_USER.LOCALE),
            status = record.get(APP_USER.STATUS)!!,
            lastLoginAt = record.get(APP_USER.LAST_LOGIN_AT),
            createdAt = record.get(APP_USER.CREATED_AT)!!,
            updatedAt = record.get(APP_USER.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(APP_USER)
            .where(APP_USER.ID.eq(id))
            .execute() > 0
}
