package cz.lbenda.reservation.booking

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class BookingRepository(private val dsl: DSLContext) {
    fun create(newBooking: NewBooking): Booking {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(BOOKING)
            .set(BOOKING.ID, newBooking.id)
            .set(BOOKING.BUSINESS_ID, newBooking.businessId)
            .set(BOOKING.LOCATION_ID, newBooking.locationId)
            .set(BOOKING.SERVICE_ID, newBooking.serviceId)
            .set(BOOKING.STAFF_ID, newBooking.staffId)
            .set(BOOKING.CLIENT_ID, newBooking.clientId)
            .set(BOOKING.PUBLIC_REF, newBooking.publicRef)
            .set(BOOKING.STATUS, newBooking.status)
            .set(BOOKING.START_AT, newBooking.startAt)
            .set(BOOKING.END_AT, newBooking.endAt)
            .set(BOOKING.TIMEZONE, newBooking.timezone)
            .set(BOOKING.PRICE_AMOUNT, newBooking.priceAmount)
            .set(BOOKING.PRICE_CURRENCY, newBooking.priceCurrency)
            .set(BOOKING.NOTES, newBooking.notes)
            .set(BOOKING.CLIENT_MESSAGE, newBooking.clientMessage)
            .set(BOOKING.CREATED_AT, now)
            .set(BOOKING.UPDATED_AT, now)
            .returning(
                BOOKING.ID,
                BOOKING.BUSINESS_ID,
                BOOKING.LOCATION_ID,
                BOOKING.SERVICE_ID,
                BOOKING.STAFF_ID,
                BOOKING.CLIENT_ID,
                BOOKING.PUBLIC_REF,
                BOOKING.STATUS,
                BOOKING.START_AT,
                BOOKING.END_AT,
                BOOKING.TIMEZONE,
                BOOKING.PRICE_AMOUNT,
                BOOKING.PRICE_CURRENCY,
                BOOKING.NOTES,
                BOOKING.CLIENT_MESSAGE,
                BOOKING.CREATED_AT,
                BOOKING.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert booking")

        return Booking(
            id = record.get(BOOKING.ID)!!,
            businessId = record.get(BOOKING.BUSINESS_ID)!!,
            locationId = record.get(BOOKING.LOCATION_ID)!!,
            serviceId = record.get(BOOKING.SERVICE_ID)!!,
            staffId = record.get(BOOKING.STAFF_ID)!!,
            clientId = record.get(BOOKING.CLIENT_ID)!!,
            publicRef = record.get(BOOKING.PUBLIC_REF)!!,
            status = record.get(BOOKING.STATUS)!!,
            startAt = record.get(BOOKING.START_AT)!!,
            endAt = record.get(BOOKING.END_AT)!!,
            timezone = record.get(BOOKING.TIMEZONE)!!,
            priceAmount = record.get(BOOKING.PRICE_AMOUNT)!!,
            priceCurrency = record.get(BOOKING.PRICE_CURRENCY)!!,
            notes = record.get(BOOKING.NOTES),
            clientMessage = record.get(BOOKING.CLIENT_MESSAGE),
            createdAt = record.get(BOOKING.CREATED_AT)!!,
            updatedAt = record.get(BOOKING.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Booking? {
        val record = dsl.select(
            BOOKING.ID,
            BOOKING.BUSINESS_ID,
            BOOKING.LOCATION_ID,
            BOOKING.SERVICE_ID,
            BOOKING.STAFF_ID,
            BOOKING.CLIENT_ID,
            BOOKING.PUBLIC_REF,
            BOOKING.STATUS,
            BOOKING.START_AT,
            BOOKING.END_AT,
            BOOKING.TIMEZONE,
            BOOKING.PRICE_AMOUNT,
            BOOKING.PRICE_CURRENCY,
            BOOKING.NOTES,
            BOOKING.CLIENT_MESSAGE,
            BOOKING.CREATED_AT,
            BOOKING.UPDATED_AT
        )
            .from(BOOKING)
            .where(BOOKING.ID.eq(id))
            .fetchOne() ?: return null

        return Booking(
            id = record.get(BOOKING.ID)!!,
            businessId = record.get(BOOKING.BUSINESS_ID)!!,
            locationId = record.get(BOOKING.LOCATION_ID)!!,
            serviceId = record.get(BOOKING.SERVICE_ID)!!,
            staffId = record.get(BOOKING.STAFF_ID)!!,
            clientId = record.get(BOOKING.CLIENT_ID)!!,
            publicRef = record.get(BOOKING.PUBLIC_REF)!!,
            status = record.get(BOOKING.STATUS)!!,
            startAt = record.get(BOOKING.START_AT)!!,
            endAt = record.get(BOOKING.END_AT)!!,
            timezone = record.get(BOOKING.TIMEZONE)!!,
            priceAmount = record.get(BOOKING.PRICE_AMOUNT)!!,
            priceCurrency = record.get(BOOKING.PRICE_CURRENCY)!!,
            notes = record.get(BOOKING.NOTES),
            clientMessage = record.get(BOOKING.CLIENT_MESSAGE),
            createdAt = record.get(BOOKING.CREATED_AT)!!,
            updatedAt = record.get(BOOKING.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: BookingUpdate): Booking? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(BOOKING)
            .set(BOOKING.STATUS, update.status)
            .set(BOOKING.START_AT, update.startAt)
            .set(BOOKING.END_AT, update.endAt)
            .set(BOOKING.TIMEZONE, update.timezone)
            .set(BOOKING.PRICE_AMOUNT, update.priceAmount)
            .set(BOOKING.PRICE_CURRENCY, update.priceCurrency)
            .set(BOOKING.NOTES, update.notes)
            .set(BOOKING.CLIENT_MESSAGE, update.clientMessage)
            .set(BOOKING.UPDATED_AT, now)
            .where(BOOKING.ID.eq(id))
            .returning(
                BOOKING.ID,
                BOOKING.BUSINESS_ID,
                BOOKING.LOCATION_ID,
                BOOKING.SERVICE_ID,
                BOOKING.STAFF_ID,
                BOOKING.CLIENT_ID,
                BOOKING.PUBLIC_REF,
                BOOKING.STATUS,
                BOOKING.START_AT,
                BOOKING.END_AT,
                BOOKING.TIMEZONE,
                BOOKING.PRICE_AMOUNT,
                BOOKING.PRICE_CURRENCY,
                BOOKING.NOTES,
                BOOKING.CLIENT_MESSAGE,
                BOOKING.CREATED_AT,
                BOOKING.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Booking(
            id = record.get(BOOKING.ID)!!,
            businessId = record.get(BOOKING.BUSINESS_ID)!!,
            locationId = record.get(BOOKING.LOCATION_ID)!!,
            serviceId = record.get(BOOKING.SERVICE_ID)!!,
            staffId = record.get(BOOKING.STAFF_ID)!!,
            clientId = record.get(BOOKING.CLIENT_ID)!!,
            publicRef = record.get(BOOKING.PUBLIC_REF)!!,
            status = record.get(BOOKING.STATUS)!!,
            startAt = record.get(BOOKING.START_AT)!!,
            endAt = record.get(BOOKING.END_AT)!!,
            timezone = record.get(BOOKING.TIMEZONE)!!,
            priceAmount = record.get(BOOKING.PRICE_AMOUNT)!!,
            priceCurrency = record.get(BOOKING.PRICE_CURRENCY)!!,
            notes = record.get(BOOKING.NOTES),
            clientMessage = record.get(BOOKING.CLIENT_MESSAGE),
            createdAt = record.get(BOOKING.CREATED_AT)!!,
            updatedAt = record.get(BOOKING.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(BOOKING)
            .where(BOOKING.ID.eq(id))
            .execute() > 0
}
