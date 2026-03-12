package cz.lbenda.reservation.catalog

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class StaffCommand(
    val locationId: UUID,
    val displayName: String,
    val email: String?,
    val phone: String?,
    val bio: String?,
    val status: String
)

data class StaffWeeklyScheduleCommand(
    val dayOfWeek: Int,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class StaffScheduleExceptionCommand(
    val exceptionDate: LocalDate,
    val rangeType: StaffScheduleRangeType,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val note: String?
)

data class StaffValidationError(
    val field: String,
    val message: String
)

class StaffValidationException(
    val errors: List<StaffValidationError>
) : IllegalArgumentException("Staff validation failed")

interface StaffManagementService {
    fun create(businessId: UUID, command: StaffCommand): Staff
    fun get(businessId: UUID, staffId: UUID): Staff?
    fun list(businessId: UUID, status: String? = null): List<Staff>
    fun update(businessId: UUID, staffId: UUID, command: StaffCommand): Staff?
    fun listWeeklySchedules(businessId: UUID, staffId: UUID): List<StaffWeeklySchedule>
    fun createWeeklySchedule(businessId: UUID, staffId: UUID, command: StaffWeeklyScheduleCommand): StaffWeeklySchedule
    fun updateWeeklySchedule(businessId: UUID, staffId: UUID, scheduleId: UUID, command: StaffWeeklyScheduleCommand): StaffWeeklySchedule?
    fun deleteWeeklySchedule(businessId: UUID, staffId: UUID, scheduleId: UUID): Boolean
    fun listScheduleExceptions(businessId: UUID, staffId: UUID): List<StaffScheduleException>
    fun createScheduleException(businessId: UUID, staffId: UUID, command: StaffScheduleExceptionCommand): StaffScheduleException
    fun updateScheduleException(businessId: UUID, staffId: UUID, exceptionId: UUID, command: StaffScheduleExceptionCommand): StaffScheduleException?
    fun deleteScheduleException(businessId: UUID, staffId: UUID, exceptionId: UUID): Boolean
}

class DefaultStaffManagementService(
    private val staffRepository: StaffRepository,
    private val staffWeeklyScheduleRepository: StaffWeeklyScheduleRepository,
    private val staffScheduleExceptionRepository: StaffScheduleExceptionRepository
) : StaffManagementService {
    override fun create(businessId: UUID, command: StaffCommand): Staff {
        validateStaff(command)
        return staffRepository.create(
            NewStaff(
                id = UUID.randomUUID(),
                businessId = businessId,
                locationId = command.locationId,
                displayName = command.displayName.trim(),
                email = command.email?.trim()?.ifBlank { null },
                phone = command.phone?.trim()?.ifBlank { null },
                bio = command.bio?.trim()?.ifBlank { null },
                status = command.status.trim().lowercase()
            )
        )
    }

    override fun get(businessId: UUID, staffId: UUID): Staff? =
        staffRepository.findById(businessId, staffId)

    override fun list(businessId: UUID, status: String?): List<Staff> =
        staffRepository.listByBusiness(businessId, status?.trim()?.lowercase())

    override fun update(businessId: UUID, staffId: UUID, command: StaffCommand): Staff? {
        validateStaff(command)
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return null
        return staffRepository.update(
            businessId = businessId,
            id = staffId,
            update = StaffUpdate(
                displayName = command.displayName.trim(),
                email = command.email?.trim()?.ifBlank { null },
                phone = command.phone?.trim()?.ifBlank { null },
                bio = command.bio?.trim()?.ifBlank { null },
                status = command.status.trim().lowercase()
            )
        )
    }

    override fun listWeeklySchedules(businessId: UUID, staffId: UUID): List<StaffWeeklySchedule> {
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return emptyList()
        return staffWeeklyScheduleRepository.listByStaffId(staffId)
    }

    override fun createWeeklySchedule(
        businessId: UUID,
        staffId: UUID,
        command: StaffWeeklyScheduleCommand
    ): StaffWeeklySchedule {
        validateWeeklySchedule(command)
        ensureStaffBelongsToBusiness(businessId, staffId)
            ?: throw IllegalArgumentException("Staff not found")
        return staffWeeklyScheduleRepository.create(
            NewStaffWeeklySchedule(
                id = UUID.randomUUID(),
                staffId = staffId,
                dayOfWeek = command.dayOfWeek,
                rangeType = command.rangeType,
                startTime = command.startTime,
                endTime = command.endTime
            )
        )
    }

    override fun updateWeeklySchedule(
        businessId: UUID,
        staffId: UUID,
        scheduleId: UUID,
        command: StaffWeeklyScheduleCommand
    ): StaffWeeklySchedule? {
        validateWeeklySchedule(command)
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return null
        val existing = staffWeeklyScheduleRepository.findById(scheduleId) ?: return null
        if (existing.staffId != staffId) return null
        return staffWeeklyScheduleRepository.update(
            scheduleId,
            StaffWeeklyScheduleUpdate(
                dayOfWeek = command.dayOfWeek,
                rangeType = command.rangeType,
                startTime = command.startTime,
                endTime = command.endTime
            )
        )
    }

    override fun deleteWeeklySchedule(businessId: UUID, staffId: UUID, scheduleId: UUID): Boolean {
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return false
        val existing = staffWeeklyScheduleRepository.findById(scheduleId) ?: return false
        if (existing.staffId != staffId) return false
        return staffWeeklyScheduleRepository.delete(scheduleId)
    }

    override fun listScheduleExceptions(businessId: UUID, staffId: UUID): List<StaffScheduleException> {
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return emptyList()
        return staffScheduleExceptionRepository.listByStaffId(staffId)
    }

    override fun createScheduleException(
        businessId: UUID,
        staffId: UUID,
        command: StaffScheduleExceptionCommand
    ): StaffScheduleException {
        validateScheduleException(command)
        ensureStaffBelongsToBusiness(businessId, staffId)
            ?: throw IllegalArgumentException("Staff not found")
        return staffScheduleExceptionRepository.create(
            NewStaffScheduleException(
                id = UUID.randomUUID(),
                staffId = staffId,
                exceptionDate = command.exceptionDate,
                rangeType = command.rangeType,
                startTime = command.startTime,
                endTime = command.endTime,
                note = command.note?.trim()?.ifBlank { null }
            )
        )
    }

    override fun updateScheduleException(
        businessId: UUID,
        staffId: UUID,
        exceptionId: UUID,
        command: StaffScheduleExceptionCommand
    ): StaffScheduleException? {
        validateScheduleException(command)
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return null
        val existing = staffScheduleExceptionRepository.findById(exceptionId) ?: return null
        if (existing.staffId != staffId) return null
        return staffScheduleExceptionRepository.update(
            exceptionId,
            StaffScheduleExceptionUpdate(
                exceptionDate = command.exceptionDate,
                rangeType = command.rangeType,
                startTime = command.startTime,
                endTime = command.endTime,
                note = command.note?.trim()?.ifBlank { null }
            )
        )
    }

    override fun deleteScheduleException(businessId: UUID, staffId: UUID, exceptionId: UUID): Boolean {
        ensureStaffBelongsToBusiness(businessId, staffId) ?: return false
        val existing = staffScheduleExceptionRepository.findById(exceptionId) ?: return false
        if (existing.staffId != staffId) return false
        return staffScheduleExceptionRepository.delete(exceptionId)
    }

    private fun ensureStaffBelongsToBusiness(businessId: UUID, staffId: UUID): Staff? =
        staffRepository.findById(businessId, staffId)

    private fun validateStaff(command: StaffCommand) {
        val normalizedStatus = command.status.trim().lowercase()
        val errors = buildList {
            if (command.displayName.isBlank()) {
                add(StaffValidationError("displayName", "must not be blank"))
            }
            if (normalizedStatus !in setOf("active", "inactive")) {
                add(StaffValidationError("status", "must be active or inactive"))
            }
            if (command.email != null && command.email.isNotBlank() && !command.email.contains("@")) {
                add(StaffValidationError("email", "must be a valid email address"))
            }
        }
        if (errors.isNotEmpty()) {
            throw StaffValidationException(errors)
        }
    }

    private fun validateWeeklySchedule(command: StaffWeeklyScheduleCommand) {
        val errors = buildList {
            if (command.dayOfWeek !in 1..7) {
                add(StaffValidationError("dayOfWeek", "must be between 1 and 7"))
            }
            if (command.rangeType == StaffScheduleRangeType.DAY_OFF) {
                add(StaffValidationError("rangeType", "weekly schedules support only WORK or BREAK"))
            }
            if (command.startTime >= command.endTime) {
                add(StaffValidationError("endTime", "must be later than startTime"))
            }
        }
        if (errors.isNotEmpty()) {
            throw StaffValidationException(errors)
        }
    }

    private fun validateScheduleException(command: StaffScheduleExceptionCommand) {
        val errors = buildList {
            if (command.rangeType == StaffScheduleRangeType.DAY_OFF) {
                if (command.startTime != null || command.endTime != null) {
                    add(StaffValidationError("rangeType", "DAY_OFF must not define startTime or endTime"))
                }
            } else {
                if (command.startTime == null) {
                    add(StaffValidationError("startTime", "is required"))
                }
                if (command.endTime == null) {
                    add(StaffValidationError("endTime", "is required"))
                }
                if (command.startTime != null && command.endTime != null && command.startTime >= command.endTime) {
                    add(StaffValidationError("endTime", "must be later than startTime"))
                }
            }
        }
        if (errors.isNotEmpty()) {
            throw StaffValidationException(errors)
        }
    }
}
