package cz.lbenda.reservation

import cz.lbenda.reservation.availability.AvailabilitySlotService
import cz.lbenda.reservation.availability.DefaultAvailabilityConflictChecker
import cz.lbenda.reservation.availability.DefaultAvailabilitySlotService
import cz.lbenda.reservation.availability.DefaultStaffAvailabilityScheduleService
import cz.lbenda.reservation.availability.DefaultStaffOccupiedTimeService
import cz.lbenda.reservation.catalog.DefaultServiceCatalogService
import cz.lbenda.reservation.catalog.DefaultStaffManagementService
import cz.lbenda.reservation.catalog.ServiceCatalogService
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffManagementService
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffServiceRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import cz.lbenda.reservation.booking.BlockRepository
import cz.lbenda.reservation.booking.BookingRepository
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

    fun createAvailabilitySlotService(): AvailabilitySlotService {
        val dsl = createDsl()
        val staffRepository = StaffRepository(dsl)
        val serviceRepository = ServiceRepository(dsl)
        return DefaultAvailabilitySlotService(
            staffRepository = staffRepository,
            staffServiceRepository = StaffServiceRepository(dsl),
            serviceRepository = serviceRepository,
            staffAvailabilityScheduleService = DefaultStaffAvailabilityScheduleService(
                staffRepository = staffRepository,
                staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(dsl),
                staffScheduleExceptionRepository = StaffScheduleExceptionRepository(dsl)
            ),
            staffOccupiedTimeService = DefaultStaffOccupiedTimeService(
                staffRepository = staffRepository,
                bookingRepository = BookingRepository(dsl),
                blockRepository = BlockRepository(dsl),
                serviceRepository = serviceRepository
            ),
            availabilityConflictChecker = DefaultAvailabilityConflictChecker()
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
