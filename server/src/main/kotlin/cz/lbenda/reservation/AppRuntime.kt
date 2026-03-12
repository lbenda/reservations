package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.DefaultServiceCatalogService
import cz.lbenda.reservation.catalog.DefaultStaffManagementService
import cz.lbenda.reservation.catalog.ServiceCatalogService
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffManagementService
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import cz.lbenda.reservation.db.DataSourceFactory
import cz.lbenda.reservation.db.DbSettings
import cz.lbenda.reservation.db.FlywayMigrator
import cz.lbenda.reservation.db.JooqContextFactory

object AppRuntime {
    fun createServiceCatalogService(): ServiceCatalogService {
        val dsl = createDsl()
        return DefaultServiceCatalogService(ServiceRepository(dsl))
    }

    fun createStaffManagementService(): StaffManagementService {
        val dsl = createDsl()
        return DefaultStaffManagementService(
            staffRepository = StaffRepository(dsl),
            staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(dsl),
            staffScheduleExceptionRepository = StaffScheduleExceptionRepository(dsl)
        )
    }

    private fun createDsl() : org.jooq.DSLContext {
        val settings = DbSettings(
            url = System.getenv("RESERVATION_DB_URL")
                ?: error("Missing RESERVATION_DB_URL"),
            user = System.getenv("RESERVATION_DB_USER")
                ?: error("Missing RESERVATION_DB_USER"),
            password = System.getenv("RESERVATION_DB_PASSWORD")
                ?: error("Missing RESERVATION_DB_PASSWORD")
        )
        val dataSource = DataSourceFactory.create(settings)
        FlywayMigrator.migrate(dataSource)
        return JooqContextFactory.create(dataSource)
    }
}
