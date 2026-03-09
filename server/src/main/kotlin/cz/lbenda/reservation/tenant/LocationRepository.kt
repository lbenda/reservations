package cz.lbenda.reservation.tenant

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class LocationRepository(private val dsl: DSLContext) {
    fun create(newLocation: NewLocation): Location {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(LOCATION)
            .set(LOCATION.ID, newLocation.id)
            .set(LOCATION.BUSINESS_ID, newLocation.businessId)
            .set(LOCATION.SLUG, newLocation.slug)
            .set(LOCATION.NAME, newLocation.name)
            .set(LOCATION.ADDRESS_LINE1, newLocation.addressLine1)
            .set(LOCATION.ADDRESS_LINE2, newLocation.addressLine2)
            .set(LOCATION.CITY, newLocation.city)
            .set(LOCATION.POSTAL_CODE, newLocation.postalCode)
            .set(LOCATION.COUNTRY_CODE, newLocation.countryCode)
            .set(LOCATION.PHONE, newLocation.phone)
            .set(LOCATION.EMAIL, newLocation.email)
            .set(LOCATION.TIMEZONE, newLocation.timezone)
            .set(LOCATION.STATUS, newLocation.status)
            .set(LOCATION.CREATED_AT, now)
            .set(LOCATION.UPDATED_AT, now)
            .returning(
                LOCATION.ID,
                LOCATION.BUSINESS_ID,
                LOCATION.SLUG,
                LOCATION.NAME,
                LOCATION.ADDRESS_LINE1,
                LOCATION.ADDRESS_LINE2,
                LOCATION.CITY,
                LOCATION.POSTAL_CODE,
                LOCATION.COUNTRY_CODE,
                LOCATION.PHONE,
                LOCATION.EMAIL,
                LOCATION.TIMEZONE,
                LOCATION.STATUS,
                LOCATION.CREATED_AT,
                LOCATION.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert location")

        return Location(
            id = record.get(LOCATION.ID)!!,
            businessId = record.get(LOCATION.BUSINESS_ID)!!,
            slug = record.get(LOCATION.SLUG)!!,
            name = record.get(LOCATION.NAME)!!,
            addressLine1 = record.get(LOCATION.ADDRESS_LINE1)!!,
            addressLine2 = record.get(LOCATION.ADDRESS_LINE2),
            city = record.get(LOCATION.CITY)!!,
            postalCode = record.get(LOCATION.POSTAL_CODE)!!,
            countryCode = record.get(LOCATION.COUNTRY_CODE)!!,
            phone = record.get(LOCATION.PHONE),
            email = record.get(LOCATION.EMAIL),
            timezone = record.get(LOCATION.TIMEZONE),
            status = record.get(LOCATION.STATUS)!!,
            createdAt = record.get(LOCATION.CREATED_AT)!!,
            updatedAt = record.get(LOCATION.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Location? {
        val record = dsl.select(
            LOCATION.ID,
            LOCATION.BUSINESS_ID,
            LOCATION.SLUG,
            LOCATION.NAME,
            LOCATION.ADDRESS_LINE1,
            LOCATION.ADDRESS_LINE2,
            LOCATION.CITY,
            LOCATION.POSTAL_CODE,
            LOCATION.COUNTRY_CODE,
            LOCATION.PHONE,
            LOCATION.EMAIL,
            LOCATION.TIMEZONE,
            LOCATION.STATUS,
            LOCATION.CREATED_AT,
            LOCATION.UPDATED_AT
        )
            .from(LOCATION)
            .where(LOCATION.ID.eq(id))
            .fetchOne() ?: return null

        return Location(
            id = record.get(LOCATION.ID)!!,
            businessId = record.get(LOCATION.BUSINESS_ID)!!,
            slug = record.get(LOCATION.SLUG)!!,
            name = record.get(LOCATION.NAME)!!,
            addressLine1 = record.get(LOCATION.ADDRESS_LINE1)!!,
            addressLine2 = record.get(LOCATION.ADDRESS_LINE2),
            city = record.get(LOCATION.CITY)!!,
            postalCode = record.get(LOCATION.POSTAL_CODE)!!,
            countryCode = record.get(LOCATION.COUNTRY_CODE)!!,
            phone = record.get(LOCATION.PHONE),
            email = record.get(LOCATION.EMAIL),
            timezone = record.get(LOCATION.TIMEZONE),
            status = record.get(LOCATION.STATUS)!!,
            createdAt = record.get(LOCATION.CREATED_AT)!!,
            updatedAt = record.get(LOCATION.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: LocationUpdate): Location? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(LOCATION)
            .set(LOCATION.SLUG, update.slug)
            .set(LOCATION.NAME, update.name)
            .set(LOCATION.ADDRESS_LINE1, update.addressLine1)
            .set(LOCATION.ADDRESS_LINE2, update.addressLine2)
            .set(LOCATION.CITY, update.city)
            .set(LOCATION.POSTAL_CODE, update.postalCode)
            .set(LOCATION.COUNTRY_CODE, update.countryCode)
            .set(LOCATION.PHONE, update.phone)
            .set(LOCATION.EMAIL, update.email)
            .set(LOCATION.TIMEZONE, update.timezone)
            .set(LOCATION.STATUS, update.status)
            .set(LOCATION.UPDATED_AT, now)
            .where(LOCATION.ID.eq(id))
            .returning(
                LOCATION.ID,
                LOCATION.BUSINESS_ID,
                LOCATION.SLUG,
                LOCATION.NAME,
                LOCATION.ADDRESS_LINE1,
                LOCATION.ADDRESS_LINE2,
                LOCATION.CITY,
                LOCATION.POSTAL_CODE,
                LOCATION.COUNTRY_CODE,
                LOCATION.PHONE,
                LOCATION.EMAIL,
                LOCATION.TIMEZONE,
                LOCATION.STATUS,
                LOCATION.CREATED_AT,
                LOCATION.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Location(
            id = record.get(LOCATION.ID)!!,
            businessId = record.get(LOCATION.BUSINESS_ID)!!,
            slug = record.get(LOCATION.SLUG)!!,
            name = record.get(LOCATION.NAME)!!,
            addressLine1 = record.get(LOCATION.ADDRESS_LINE1)!!,
            addressLine2 = record.get(LOCATION.ADDRESS_LINE2),
            city = record.get(LOCATION.CITY)!!,
            postalCode = record.get(LOCATION.POSTAL_CODE)!!,
            countryCode = record.get(LOCATION.COUNTRY_CODE)!!,
            phone = record.get(LOCATION.PHONE),
            email = record.get(LOCATION.EMAIL),
            timezone = record.get(LOCATION.TIMEZONE),
            status = record.get(LOCATION.STATUS)!!,
            createdAt = record.get(LOCATION.CREATED_AT)!!,
            updatedAt = record.get(LOCATION.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(LOCATION)
            .where(LOCATION.ID.eq(id))
            .execute() > 0
}
