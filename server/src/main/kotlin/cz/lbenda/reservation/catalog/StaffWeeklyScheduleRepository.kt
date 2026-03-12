package cz.lbenda.reservation.catalog

import cz.lbenda.reservation.jooq.tables.references.STAFF_WEEKLY_SCHEDULE
import org.jooq.DSLContext
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class StaffWeeklyScheduleRepository(private val dsl: DSLContext) {
    fun create(newSchedule: NewStaffWeeklySchedule): StaffWeeklySchedule {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(STAFF_WEEKLY_SCHEDULE)
            .set(STAFF_WEEKLY_SCHEDULE.ID, newSchedule.id)
            .set(STAFF_WEEKLY_SCHEDULE.STAFF_ID, newSchedule.staffId)
            .set(STAFF_WEEKLY_SCHEDULE.DAY_OF_WEEK, newSchedule.dayOfWeek.toShort())
            .set(STAFF_WEEKLY_SCHEDULE.RANGE_TYPE, newSchedule.rangeType.name)
            .set(STAFF_WEEKLY_SCHEDULE.START_TIME, newSchedule.startTime)
            .set(STAFF_WEEKLY_SCHEDULE.END_TIME, newSchedule.endTime)
            .set(STAFF_WEEKLY_SCHEDULE.CREATED_AT, now)
            .set(STAFF_WEEKLY_SCHEDULE.UPDATED_AT, now)
            .returning()
            .fetchOne() ?: error("Failed to insert staff weekly schedule")

        return toModel(record)
    }

    fun findById(id: UUID): StaffWeeklySchedule? =
        dsl.selectFrom(STAFF_WEEKLY_SCHEDULE)
            .where(STAFF_WEEKLY_SCHEDULE.ID.eq(id))
            .fetchOne()
            ?.let(::toModel)

    fun listByStaffId(staffId: UUID): List<StaffWeeklySchedule> =
        dsl.selectFrom(STAFF_WEEKLY_SCHEDULE)
            .where(STAFF_WEEKLY_SCHEDULE.STAFF_ID.eq(staffId))
            .orderBy(
                STAFF_WEEKLY_SCHEDULE.DAY_OF_WEEK.asc(),
                STAFF_WEEKLY_SCHEDULE.START_TIME.asc(),
                STAFF_WEEKLY_SCHEDULE.END_TIME.asc()
            )
            .fetch { toModel(it) }

    fun update(id: UUID, update: StaffWeeklyScheduleUpdate): StaffWeeklySchedule? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        return dsl.update(STAFF_WEEKLY_SCHEDULE)
            .set(STAFF_WEEKLY_SCHEDULE.DAY_OF_WEEK, update.dayOfWeek.toShort())
            .set(STAFF_WEEKLY_SCHEDULE.RANGE_TYPE, update.rangeType.name)
            .set(STAFF_WEEKLY_SCHEDULE.START_TIME, update.startTime)
            .set(STAFF_WEEKLY_SCHEDULE.END_TIME, update.endTime)
            .set(STAFF_WEEKLY_SCHEDULE.UPDATED_AT, now)
            .where(STAFF_WEEKLY_SCHEDULE.ID.eq(id))
            .returning()
            .fetchOne()
            ?.let(::toModel)
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(STAFF_WEEKLY_SCHEDULE)
            .where(STAFF_WEEKLY_SCHEDULE.ID.eq(id))
            .execute() > 0

    private fun toModel(record: org.jooq.Record): StaffWeeklySchedule =
        StaffWeeklySchedule(
            id = record.get(STAFF_WEEKLY_SCHEDULE.ID)!!,
            staffId = record.get(STAFF_WEEKLY_SCHEDULE.STAFF_ID)!!,
            dayOfWeek = record.get(STAFF_WEEKLY_SCHEDULE.DAY_OF_WEEK)!!.toInt(),
            rangeType = StaffScheduleRangeType.valueOf(record.get(STAFF_WEEKLY_SCHEDULE.RANGE_TYPE)!!),
            startTime = record.get(STAFF_WEEKLY_SCHEDULE.START_TIME)!!,
            endTime = record.get(STAFF_WEEKLY_SCHEDULE.END_TIME)!!,
            createdAt = record.get(STAFF_WEEKLY_SCHEDULE.CREATED_AT)!!,
            updatedAt = record.get(STAFF_WEEKLY_SCHEDULE.UPDATED_AT)!!
        )
}
