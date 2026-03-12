package cz.lbenda.reservation

import cz.lbenda.reservation.catalog.DefaultServiceCatalogService
import cz.lbenda.reservation.catalog.DefaultStaffManagementService
import cz.lbenda.reservation.catalog.ServiceRepository
import cz.lbenda.reservation.catalog.StaffRepository
import cz.lbenda.reservation.catalog.StaffScheduleExceptionRepository
import cz.lbenda.reservation.catalog.StaffWeeklyScheduleRepository
import cz.lbenda.reservation.db.TestDatabase

object TestServiceFactory {
    fun serviceCatalogService() = DefaultServiceCatalogService(ServiceRepository(TestDatabase.dsl))

    fun staffManagementService() = DefaultStaffManagementService(
        staffRepository = StaffRepository(TestDatabase.dsl),
        staffWeeklyScheduleRepository = StaffWeeklyScheduleRepository(TestDatabase.dsl),
        staffScheduleExceptionRepository = StaffScheduleExceptionRepository(TestDatabase.dsl)
    )
}
