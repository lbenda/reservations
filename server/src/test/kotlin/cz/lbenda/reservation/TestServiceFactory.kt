package cz.lbenda.reservation

import cz.lbenda.reservation.availability.DefaultAvailabilityConflictChecker
import cz.lbenda.reservation.availability.DefaultAvailabilitySlotService
import cz.lbenda.reservation.availability.DefaultStaffAvailabilityScheduleService
import cz.lbenda.reservation.availability.DefaultStaffOccupiedTimeService
import cz.lbenda.reservation.catalog.DefaultServiceCatalogService
import cz.lbenda.reservation.catalog.DefaultStaffManagementService
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffServiceRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import cz.lbenda.reservation.booking.BlockRepository
import cz.lbenda.reservation.booking.BookingRepository
import cz.lbenda.reservation.db.TestDatabase

object TestServiceFactory {
    fun serviceCatalogService() = DefaultServiceCatalogService(ServiceRepository(TestDatabase.dsl))

    fun staffManagementService() = DefaultStaffManagementService(
        staffRepository = StaffRepository(TestDatabase.dsl),
        staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(TestDatabase.dsl),
        staffScheduleExceptionRepository = StaffScheduleExceptionRepository(TestDatabase.dsl)
    )

    fun availabilitySlotService(): DefaultAvailabilitySlotService {
        val staffRepository = StaffRepository(TestDatabase.dsl)
        val serviceRepository = ServiceRepository(TestDatabase.dsl)
        return DefaultAvailabilitySlotService(
            staffRepository = staffRepository,
            staffServiceRepository = StaffServiceRepository(TestDatabase.dsl),
            serviceRepository = serviceRepository,
            staffAvailabilityScheduleService = DefaultStaffAvailabilityScheduleService(
                staffRepository = staffRepository,
                staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(TestDatabase.dsl),
                staffScheduleExceptionRepository = StaffScheduleExceptionRepository(TestDatabase.dsl)
            ),
            staffOccupiedTimeService = DefaultStaffOccupiedTimeService(
                staffRepository = staffRepository,
                bookingRepository = BookingRepository(TestDatabase.dsl),
                blockRepository = BlockRepository(TestDatabase.dsl),
                serviceRepository = serviceRepository
            ),
            availabilityConflictChecker = DefaultAvailabilityConflictChecker()
        )
    }
}
