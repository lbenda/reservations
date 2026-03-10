package cz.lbenda.reservation.payments

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class PaymentRepository(private val dsl: DSLContext) {
    fun create(newPayment: NewPayment): Payment {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(PAYMENT)
            .set(PAYMENT.ID, newPayment.id)
            .set(PAYMENT.BUSINESS_ID, newPayment.businessId)
            .set(PAYMENT.BOOKING_ID, newPayment.bookingId)
            .set(PAYMENT.PROVIDER_REF, newPayment.providerRef)
            .set(PAYMENT.PROVIDER_NAME, newPayment.providerName)
            .set(PAYMENT.AMOUNT, newPayment.amount)
            .set(PAYMENT.CURRENCY, newPayment.currency)
            .set(PAYMENT.STATUS, newPayment.status)
            .set(PAYMENT.PAID_AT, newPayment.paidAt)
            .set(PAYMENT.CREATED_AT, now)
            .set(PAYMENT.UPDATED_AT, now)
            .returning(
                PAYMENT.ID,
                PAYMENT.BUSINESS_ID,
                PAYMENT.BOOKING_ID,
                PAYMENT.PROVIDER_REF,
                PAYMENT.PROVIDER_NAME,
                PAYMENT.AMOUNT,
                PAYMENT.CURRENCY,
                PAYMENT.STATUS,
                PAYMENT.PAID_AT,
                PAYMENT.CREATED_AT,
                PAYMENT.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert payment")

        return Payment(
            id = record.get(PAYMENT.ID)!!,
            businessId = record.get(PAYMENT.BUSINESS_ID)!!,
            bookingId = record.get(PAYMENT.BOOKING_ID)!!,
            providerRef = record.get(PAYMENT.PROVIDER_REF)!!,
            providerName = record.get(PAYMENT.PROVIDER_NAME)!!,
            amount = record.get(PAYMENT.AMOUNT)!!,
            currency = record.get(PAYMENT.CURRENCY)!!,
            status = record.get(PAYMENT.STATUS)!!,
            paidAt = record.get(PAYMENT.PAID_AT),
            createdAt = record.get(PAYMENT.CREATED_AT)!!,
            updatedAt = record.get(PAYMENT.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Payment? {
        val record = dsl.select(
            PAYMENT.ID,
            PAYMENT.BUSINESS_ID,
            PAYMENT.BOOKING_ID,
            PAYMENT.PROVIDER_REF,
            PAYMENT.PROVIDER_NAME,
            PAYMENT.AMOUNT,
            PAYMENT.CURRENCY,
            PAYMENT.STATUS,
            PAYMENT.PAID_AT,
            PAYMENT.CREATED_AT,
            PAYMENT.UPDATED_AT
        )
            .from(PAYMENT)
            .where(PAYMENT.ID.eq(id))
            .fetchOne() ?: return null

        return Payment(
            id = record.get(PAYMENT.ID)!!,
            businessId = record.get(PAYMENT.BUSINESS_ID)!!,
            bookingId = record.get(PAYMENT.BOOKING_ID)!!,
            providerRef = record.get(PAYMENT.PROVIDER_REF)!!,
            providerName = record.get(PAYMENT.PROVIDER_NAME)!!,
            amount = record.get(PAYMENT.AMOUNT)!!,
            currency = record.get(PAYMENT.CURRENCY)!!,
            status = record.get(PAYMENT.STATUS)!!,
            paidAt = record.get(PAYMENT.PAID_AT),
            createdAt = record.get(PAYMENT.CREATED_AT)!!,
            updatedAt = record.get(PAYMENT.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: PaymentUpdate): Payment? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(PAYMENT)
            .set(PAYMENT.PROVIDER_NAME, update.providerName)
            .set(PAYMENT.AMOUNT, update.amount)
            .set(PAYMENT.CURRENCY, update.currency)
            .set(PAYMENT.STATUS, update.status)
            .set(PAYMENT.PAID_AT, update.paidAt)
            .set(PAYMENT.UPDATED_AT, now)
            .where(PAYMENT.ID.eq(id))
            .returning(
                PAYMENT.ID,
                PAYMENT.BUSINESS_ID,
                PAYMENT.BOOKING_ID,
                PAYMENT.PROVIDER_REF,
                PAYMENT.PROVIDER_NAME,
                PAYMENT.AMOUNT,
                PAYMENT.CURRENCY,
                PAYMENT.STATUS,
                PAYMENT.PAID_AT,
                PAYMENT.CREATED_AT,
                PAYMENT.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Payment(
            id = record.get(PAYMENT.ID)!!,
            businessId = record.get(PAYMENT.BUSINESS_ID)!!,
            bookingId = record.get(PAYMENT.BOOKING_ID)!!,
            providerRef = record.get(PAYMENT.PROVIDER_REF)!!,
            providerName = record.get(PAYMENT.PROVIDER_NAME)!!,
            amount = record.get(PAYMENT.AMOUNT)!!,
            currency = record.get(PAYMENT.CURRENCY)!!,
            status = record.get(PAYMENT.STATUS)!!,
            paidAt = record.get(PAYMENT.PAID_AT),
            createdAt = record.get(PAYMENT.CREATED_AT)!!,
            updatedAt = record.get(PAYMENT.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(PAYMENT)
            .where(PAYMENT.ID.eq(id))
            .execute() > 0
}
