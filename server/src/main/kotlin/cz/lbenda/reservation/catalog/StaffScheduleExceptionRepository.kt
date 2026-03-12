package cz.lbenda.reservation.catalog

import cz.lbenda.reservation.jooq.tables.references.STAFF_SCHEDULE_EXCEPTION
import org.jooq.DSLContext
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class StaffScheduleExceptionRepository(private val dsl: DSLContext) {
    fun create(newException: NewStaffScheduleException): StaffScheduleException {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(STAFF_SCHEDULE_EXCEPTION)
            .set(STAFF_SCHEDULE_EXCEPTION.ID, newException.id)
            .set(STAFF_SCHEDULE_EXCEPTION.STAFF_ID, newException.staffId)
            .set(STAFF_SCHEDULE_EXCEPTION.EXCEPTION_DATE, newException.exceptionDate)
            .set(STAFF_SCHEDULE_EXCEPTION.RANGE_TYPE, newException.rangeType.name)
            .set(STAFF_SCHEDULE_EXCEPTION.START_TIME, newException.startTime)
            .set(STAFF_SCHEDULE_EXCEPTION.END_TIME, newException.endTime)
            .set(STAFF_SCHEDULE_EXCEPTION.NOTE, newException.note)
            .set(STAFF_SCHEDULE_EXCEPTION.CREATED_AT, now)
            .set(STAFF_SCHEDULE_EXCEPTION.UPDATED_AT, now)
            .returning()
            .fetchOne() ?: error("Failed to insert staff schedule exception")

        return toModel(record)
    }

    fun findById(id: UUID): StaffScheduleException? =
        dsl.selectFrom(STAFF_SCHEDULE_EXCEPTION)
            .where(STAFF_SCHEDULE_EXCEPTION.ID.eq(id))
            .fetchOne()
            ?.let(::toModel)

    fun listByStaffId(staffId: UUID): List<StaffScheduleException> =
        dsl.selectFrom(STAFF_SCHEDULE_EXCEPTION)
            .where(STAFF_SCHEDULE_EXCEPTION.STAFF_ID.eq(staffId))
            .orderBy(
                STAFF_SCHEDULE_EXCEPTION.EXCEPTION_DATE.asc(),
                STAFF_SCHEDULE_EXCEPTION.START_TIME.asc().nullsFirst(),
                STAFF_SCHEDULE_EXCEPTION.END_TIME.asc().nullsFirst()
            )
            .fetch { toModel(it) }

    fun update(id: UUID, update: StaffScheduleExceptionUpdate): StaffScheduleException? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        return dsl.update(STAFF_SCHEDULE_EXCEPTION)
            .set(STAFF_SCHEDULE_EXCEPTION.EXCEPTION_DATE, update.exceptionDate)
            .set(STAFF_SCHEDULE_EXCEPTION.RANGE_TYPE, update.rangeType.name)
            .set(STAFF_SCHEDULE_EXCEPTION.START_TIME, update.startTime)
            .set(STAFF_SCHEDULE_EXCEPTION.END_TIME, update.endTime)
            .set(STAFF_SCHEDULE_EXCEPTION.NOTE, update.note)
            .set(STAFF_SCHEDULE_EXCEPTION.UPDATED_AT, now)
            .where(STAFF_SCHEDULE_EXCEPTION.ID.eq(id))
            .returning()
            .fetchOne()
            ?.let(::toModel)
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(STAFF_SCHEDULE_EXCEPTION)
            .where(STAFF_SCHEDULE_EXCEPTION.ID.eq(id))
            .execute() > 0

    private fun toModel(record: org.jooq.Record): StaffScheduleException =
        StaffScheduleException(
            id = record.get(STAFF_SCHEDULE_EXCEPTION.ID)!!,
            staffId = record.get(STAFF_SCHEDULE_EXCEPTION.STAFF_ID)!!,
            exceptionDate = record.get(STAFF_SCHEDULE_EXCEPTION.EXCEPTION_DATE)!!,
            rangeType = StaffScheduleRangeType.valueOf(record.get(STAFF_SCHEDULE_EXCEPTION.RANGE_TYPE)!!),
            startTime = record.get(STAFF_SCHEDULE_EXCEPTION.START_TIME),
            endTime = record.get(STAFF_SCHEDULE_EXCEPTION.END_TIME),
            note = record.get(STAFF_SCHEDULE_EXCEPTION.NOTE),
            createdAt = record.get(STAFF_SCHEDULE_EXCEPTION.CREATED_AT)!!,
            updatedAt = record.get(STAFF_SCHEDULE_EXCEPTION.UPDATED_AT)!!
        )
}
