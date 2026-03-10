package cz.lbenda.reservation.catalog

import cz.lbenda.reservation.tenant.Business
import cz.lbenda.reservation.tenant.BusinessRepository
import cz.lbenda.reservation.tenant.NewBusiness
import cz.lbenda.reservation.util.Uuid7
import org.jooq.DSLContext
import java.math.BigDecimal
import java.util.UUID

object SampleCatalogFixtures {
    val businessId: UUID = UUID.fromString("00000000-0000-0000-0000-000000000111")

    fun createBusiness(dsl: DSLContext): Business =
        BusinessRepository(dsl).create(
            NewBusiness(
                id = businessId,
                slug = "acme-catalog",
                name = "Acme Catalog",
                timezone = "Europe/Prague",
                currency = "CZK",
                status = "active"
            )
        )

    fun validCommand(
        durationMinutes: Int = 60,
        bufferBeforeMinutes: Int? = 5,
        bufferAfterMinutes: Int? = 10,
        minAdvanceMinutes: Int? = 120,
        maxAdvanceDays: Int? = 30,
        priceAmount: BigDecimal = BigDecimal("1200.00")
    ): ServiceCommand =
        ServiceCommand(
            serviceCode = "SVC-${Uuid7.new().toString().take(8)}",
            name = "Massage 60",
            description = "Relaxing massage",
            durationMinutes = durationMinutes,
            bufferBeforeMinutes = bufferBeforeMinutes,
            bufferAfterMinutes = bufferAfterMinutes,
            minAdvanceMinutes = minAdvanceMinutes,
            maxAdvanceDays = maxAdvanceDays,
            cancellationPolicy = "24 hours notice",
            priceAmount = priceAmount,
            priceCurrency = "CZK",
            isActive = true
        )
}
