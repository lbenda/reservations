package cz.lbenda.reservation.packages

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class PackageRepository(private val dsl: DSLContext) {
    fun create(newPackage: NewPackage): Package {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(PACKAGE)
            .set(PACKAGE.ID, newPackage.id)
            .set(PACKAGE.BUSINESS_ID, newPackage.businessId)
            .set(PACKAGE.PACKAGE_CODE, newPackage.packageCode)
            .set(PACKAGE.NAME, newPackage.name)
            .set(PACKAGE.DESCRIPTION, newPackage.description)
            .set(PACKAGE.TOTAL_CREDITS, newPackage.totalCredits)
            .set(PACKAGE.VALIDITY_DAYS, newPackage.validityDays)
            .set(PACKAGE.PRICE_AMOUNT, newPackage.priceAmount)
            .set(PACKAGE.PRICE_CURRENCY, newPackage.priceCurrency)
            .set(PACKAGE.IS_ACTIVE, newPackage.isActive)
            .set(PACKAGE.CREATED_AT, now)
            .set(PACKAGE.UPDATED_AT, now)
            .returning(
                PACKAGE.ID,
                PACKAGE.BUSINESS_ID,
                PACKAGE.PACKAGE_CODE,
                PACKAGE.NAME,
                PACKAGE.DESCRIPTION,
                PACKAGE.TOTAL_CREDITS,
                PACKAGE.VALIDITY_DAYS,
                PACKAGE.PRICE_AMOUNT,
                PACKAGE.PRICE_CURRENCY,
                PACKAGE.IS_ACTIVE,
                PACKAGE.CREATED_AT,
                PACKAGE.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert package")

        return Package(
            id = record.get(PACKAGE.ID)!!,
            businessId = record.get(PACKAGE.BUSINESS_ID)!!,
            packageCode = record.get(PACKAGE.PACKAGE_CODE),
            name = record.get(PACKAGE.NAME)!!,
            description = record.get(PACKAGE.DESCRIPTION),
            totalCredits = record.get(PACKAGE.TOTAL_CREDITS)!!,
            validityDays = record.get(PACKAGE.VALIDITY_DAYS),
            priceAmount = record.get(PACKAGE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(PACKAGE.PRICE_CURRENCY)!!,
            isActive = record.get(PACKAGE.IS_ACTIVE)!!,
            createdAt = record.get(PACKAGE.CREATED_AT)!!,
            updatedAt = record.get(PACKAGE.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Package? {
        val record = dsl.select(
            PACKAGE.ID,
            PACKAGE.BUSINESS_ID,
            PACKAGE.PACKAGE_CODE,
            PACKAGE.NAME,
            PACKAGE.DESCRIPTION,
            PACKAGE.TOTAL_CREDITS,
            PACKAGE.VALIDITY_DAYS,
            PACKAGE.PRICE_AMOUNT,
            PACKAGE.PRICE_CURRENCY,
            PACKAGE.IS_ACTIVE,
            PACKAGE.CREATED_AT,
            PACKAGE.UPDATED_AT
        )
            .from(PACKAGE)
            .where(PACKAGE.ID.eq(id))
            .fetchOne() ?: return null

        return Package(
            id = record.get(PACKAGE.ID)!!,
            businessId = record.get(PACKAGE.BUSINESS_ID)!!,
            packageCode = record.get(PACKAGE.PACKAGE_CODE),
            name = record.get(PACKAGE.NAME)!!,
            description = record.get(PACKAGE.DESCRIPTION),
            totalCredits = record.get(PACKAGE.TOTAL_CREDITS)!!,
            validityDays = record.get(PACKAGE.VALIDITY_DAYS),
            priceAmount = record.get(PACKAGE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(PACKAGE.PRICE_CURRENCY)!!,
            isActive = record.get(PACKAGE.IS_ACTIVE)!!,
            createdAt = record.get(PACKAGE.CREATED_AT)!!,
            updatedAt = record.get(PACKAGE.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: PackageUpdate): Package? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(PACKAGE)
            .set(PACKAGE.PACKAGE_CODE, update.packageCode)
            .set(PACKAGE.NAME, update.name)
            .set(PACKAGE.DESCRIPTION, update.description)
            .set(PACKAGE.TOTAL_CREDITS, update.totalCredits)
            .set(PACKAGE.VALIDITY_DAYS, update.validityDays)
            .set(PACKAGE.PRICE_AMOUNT, update.priceAmount)
            .set(PACKAGE.PRICE_CURRENCY, update.priceCurrency)
            .set(PACKAGE.IS_ACTIVE, update.isActive)
            .set(PACKAGE.UPDATED_AT, now)
            .where(PACKAGE.ID.eq(id))
            .returning(
                PACKAGE.ID,
                PACKAGE.BUSINESS_ID,
                PACKAGE.PACKAGE_CODE,
                PACKAGE.NAME,
                PACKAGE.DESCRIPTION,
                PACKAGE.TOTAL_CREDITS,
                PACKAGE.VALIDITY_DAYS,
                PACKAGE.PRICE_AMOUNT,
                PACKAGE.PRICE_CURRENCY,
                PACKAGE.IS_ACTIVE,
                PACKAGE.CREATED_AT,
                PACKAGE.UPDATED_AT
            )
            .fetchOne() ?: return null

        return Package(
            id = record.get(PACKAGE.ID)!!,
            businessId = record.get(PACKAGE.BUSINESS_ID)!!,
            packageCode = record.get(PACKAGE.PACKAGE_CODE),
            name = record.get(PACKAGE.NAME)!!,
            description = record.get(PACKAGE.DESCRIPTION),
            totalCredits = record.get(PACKAGE.TOTAL_CREDITS)!!,
            validityDays = record.get(PACKAGE.VALIDITY_DAYS),
            priceAmount = record.get(PACKAGE.PRICE_AMOUNT)!!,
            priceCurrency = record.get(PACKAGE.PRICE_CURRENCY)!!,
            isActive = record.get(PACKAGE.IS_ACTIVE)!!,
            createdAt = record.get(PACKAGE.CREATED_AT)!!,
            updatedAt = record.get(PACKAGE.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(PACKAGE)
            .where(PACKAGE.ID.eq(id))
            .execute() > 0
}
