package cz.lbenda.reservation.clients

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class ClientRepository(private val dsl: DSLContext) {
    fun create(newClient: NewClient): Client {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(CLIENT)
            .set(CLIENT.ID, newClient.id)
            .set(CLIENT.BUSINESS_ID, newClient.businessId)
            .set(CLIENT.EMAIL, newClient.email)
            .set(CLIENT.PHONE, newClient.phone)
            .set(CLIENT.FIRST_NAME, newClient.firstName)
            .set(CLIENT.LAST_NAME, newClient.lastName)
            .set(CLIENT.LOCALE, newClient.locale)
            .set(CLIENT.NOTES, newClient.notes)
            .set(CLIENT.STATUS, newClient.status)
            .set(CLIENT.CREATED_AT, now)
            .set(CLIENT.UPDATED_AT, now)
            .returning(
                CLIENT.ID,
                CLIENT.BUSINESS_ID,
                CLIENT.EMAIL,
                CLIENT.PHONE,
                CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME,
                CLIENT.LOCALE,
                CLIENT.NOTES,
                CLIENT.STATUS,
                CLIENT.CREATED_AT,
                CLIENT.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert client")

        return Client(
            id = record.get(CLIENT.ID)!!,
            businessId = record.get(CLIENT.BUSINESS_ID)!!,
            email = record.get(CLIENT.EMAIL),
            phone = record.get(CLIENT.PHONE),
            firstName = record.get(CLIENT.FIRST_NAME)!!,
            lastName = record.get(CLIENT.LAST_NAME)!!,
            locale = record.get(CLIENT.LOCALE),
            notes = record.get(CLIENT.NOTES),
            status = record.get(CLIENT.STATUS)!!,
            createdAt = record.get(CLIENT.CREATED_AT)!!,
            updatedAt = record.get(CLIENT.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Client? {
        val record = dsl.select(
            CLIENT.ID,
            CLIENT.BUSINESS_ID,
            CLIENT.EMAIL,
            CLIENT.PHONE,
            CLIENT.FIRST_NAME,
            CLIENT.LAST_NAME,
            CLIENT.LOCALE,
            CLIENT.NOTES,
            CLIENT.STATUS,
            CLIENT.CREATED_AT,
            CLIENT.UPDATED_AT
        )
            .from(CLIENT)
            .where(CLIENT.ID.eq(id))
            .fetchOne() ?: return null

        return Client(
            id = record.get(CLIENT.ID)!!,
            businessId = record.get(CLIENT.BUSINESS_ID)!!,
            email = record.get(CLIENT.EMAIL),
            phone = record.get(CLIENT.PHONE),
            firstName = record.get(CLIENT.FIRST_NAME)!!,
            lastName = record.get(CLIENT.LAST_NAME)!!,
            locale = record.get(CLIENT.LOCALE),
            notes = record.get(CLIENT.NOTES),
            status = record.get(CLIENT.STATUS)!!,
            createdAt = record.get(CLIENT.CREATED_AT)!!,
            updatedAt = record.get(CLIENT.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: ClientUpdate): Client? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(CLIENT)
            .set(CLIENT.EMAIL, update.email)
            .set(CLIENT.PHONE, update.phone)
            .set(CLIENT.FIRST_NAME, update.firstName)
            .set(CLIENT.LAST_NAME, update.lastName)
            .set(CLIENT.LOCALE, update.locale)
            .set(CLIENT.NOTES, update.notes)
            .set(CLIENT.STATUS, update.status)
            .set(CLIENT.UPDATED_AT, now)
            .where(CLIENT.ID.eq(id))
            .returning(
                CLIENT.ID,
                CLIENT.BUSINESS_ID,
                CLIENT.EMAIL,
                CLIENT.PHONE,
                CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME,
                CLIENT.LOCALE,
                CLIENT.NOTES,
                CLIENT.STATUS,
                CLIENT.CREATED_AT,
                CLIENT.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Client(
            id = record.get(CLIENT.ID)!!,
            businessId = record.get(CLIENT.BUSINESS_ID)!!,
            email = record.get(CLIENT.EMAIL),
            phone = record.get(CLIENT.PHONE),
            firstName = record.get(CLIENT.FIRST_NAME)!!,
            lastName = record.get(CLIENT.LAST_NAME)!!,
            locale = record.get(CLIENT.LOCALE),
            notes = record.get(CLIENT.NOTES),
            status = record.get(CLIENT.STATUS)!!,
            createdAt = record.get(CLIENT.CREATED_AT)!!,
            updatedAt = record.get(CLIENT.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(CLIENT)
            .where(CLIENT.ID.eq(id))
            .execute() > 0
}
