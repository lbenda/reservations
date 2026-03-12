package cz.lbenda.reservation.catalog

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class StaffServiceRepository(private val dsl: DSLContext) {
    fun create(newStaffService: NewStaffService): StaffService {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(STAFF_SERVICE)
            .set(STAFF_SERVICE.ID, newStaffService.id)
            .set(STAFF_SERVICE.STAFF_ID, newStaffService.staffId)
            .set(STAFF_SERVICE.SERVICE_ID, newStaffService.serviceId)
            .set(STAFF_SERVICE.STAFF_SERVICE_KEY, newStaffService.staffServiceKey)
            .set(STAFF_SERVICE.IS_ACTIVE, newStaffService.isActive)
            .set(STAFF_SERVICE.CREATED_AT, now)
            .set(STAFF_SERVICE.UPDATED_AT, now)
            .returning(
                STAFF_SERVICE.ID,
                STAFF_SERVICE.STAFF_ID,
                STAFF_SERVICE.SERVICE_ID,
                STAFF_SERVICE.STAFF_SERVICE_KEY,
                STAFF_SERVICE.IS_ACTIVE,
                STAFF_SERVICE.CREATED_AT,
                STAFF_SERVICE.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert staff service")

        return StaffService(
            id = record.get(STAFF_SERVICE.ID)!!,
            staffId = record.get(STAFF_SERVICE.STAFF_ID)!!,
            serviceId = record.get(STAFF_SERVICE.SERVICE_ID)!!,
            staffServiceKey = record.get(STAFF_SERVICE.STAFF_SERVICE_KEY),
            isActive = record.get(STAFF_SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(STAFF_SERVICE.CREATED_AT)!!,
            updatedAt = record.get(STAFF_SERVICE.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): StaffService? {
        val record = dsl.select(
            STAFF_SERVICE.ID,
            STAFF_SERVICE.STAFF_ID,
            STAFF_SERVICE.SERVICE_ID,
            STAFF_SERVICE.STAFF_SERVICE_KEY,
            STAFF_SERVICE.IS_ACTIVE,
            STAFF_SERVICE.CREATED_AT,
            STAFF_SERVICE.UPDATED_AT
        )
            .from(STAFF_SERVICE)
            .where(STAFF_SERVICE.ID.eq(id))
            .fetchOne() ?: return null

        return StaffService(
            id = record.get(STAFF_SERVICE.ID)!!,
            staffId = record.get(STAFF_SERVICE.STAFF_ID)!!,
            serviceId = record.get(STAFF_SERVICE.SERVICE_ID)!!,
            staffServiceKey = record.get(STAFF_SERVICE.STAFF_SERVICE_KEY),
            isActive = record.get(STAFF_SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(STAFF_SERVICE.CREATED_AT)!!,
            updatedAt = record.get(STAFF_SERVICE.UPDATED_AT)!!
        )
    }

    fun update(id: UUID, update: StaffServiceUpdate): StaffService? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(STAFF_SERVICE)
            .set(STAFF_SERVICE.STAFF_SERVICE_KEY, update.staffServiceKey)
            .set(STAFF_SERVICE.IS_ACTIVE, update.isActive)
            .set(STAFF_SERVICE.UPDATED_AT, now)
            .where(STAFF_SERVICE.ID.eq(id))
            .returning(
                STAFF_SERVICE.ID,
                STAFF_SERVICE.STAFF_ID,
                STAFF_SERVICE.SERVICE_ID,
                STAFF_SERVICE.STAFF_SERVICE_KEY,
                STAFF_SERVICE.IS_ACTIVE,
                STAFF_SERVICE.CREATED_AT,
                STAFF_SERVICE.UPDATED_AT
            )
            .fetchOne() ?: return null

        return StaffService(
            id = record.get(STAFF_SERVICE.ID)!!,
            staffId = record.get(STAFF_SERVICE.STAFF_ID)!!,
            serviceId = record.get(STAFF_SERVICE.SERVICE_ID)!!,
            staffServiceKey = record.get(STAFF_SERVICE.STAFF_SERVICE_KEY),
            isActive = record.get(STAFF_SERVICE.IS_ACTIVE)!!,
            createdAt = record.get(STAFF_SERVICE.CREATED_AT)!!,
            updatedAt = record.get(STAFF_SERVICE.UPDATED_AT)!!
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(STAFF_SERVICE)
            .where(STAFF_SERVICE.ID.eq(id))
            .execute() > 0

    fun listByServiceId(serviceId: UUID, isActive: Boolean? = null): List<StaffService> {
        var condition: org.jooq.Condition = STAFF_SERVICE.SERVICE_ID.eq(serviceId)
        if (isActive != null) {
            condition = condition.and(STAFF_SERVICE.IS_ACTIVE.eq(isActive))
        }

        return dsl.select(
            STAFF_SERVICE.ID,
            STAFF_SERVICE.STAFF_ID,
            STAFF_SERVICE.SERVICE_ID,
            STAFF_SERVICE.STAFF_SERVICE_KEY,
            STAFF_SERVICE.IS_ACTIVE,
            STAFF_SERVICE.CREATED_AT,
            STAFF_SERVICE.UPDATED_AT
        )
            .from(STAFF_SERVICE)
            .where(condition)
            .orderBy(STAFF_SERVICE.STAFF_ID.asc())
            .fetch()
            .map {
                StaffService(
                    id = it.get(STAFF_SERVICE.ID)!!,
                    staffId = it.get(STAFF_SERVICE.STAFF_ID)!!,
                    serviceId = it.get(STAFF_SERVICE.SERVICE_ID)!!,
                    staffServiceKey = it.get(STAFF_SERVICE.STAFF_SERVICE_KEY),
                    isActive = it.get(STAFF_SERVICE.IS_ACTIVE)!!,
                    createdAt = it.get(STAFF_SERVICE.CREATED_AT)!!,
                    updatedAt = it.get(STAFF_SERVICE.UPDATED_AT)!!
                )
            }
    }

    fun listByStaffId(staffId: UUID, isActive: Boolean? = null): List<StaffService> {
        var condition: org.jooq.Condition = STAFF_SERVICE.STAFF_ID.eq(staffId)
        if (isActive != null) {
            condition = condition.and(STAFF_SERVICE.IS_ACTIVE.eq(isActive))
        }

        return dsl.select(
            STAFF_SERVICE.ID,
            STAFF_SERVICE.STAFF_ID,
            STAFF_SERVICE.SERVICE_ID,
            STAFF_SERVICE.STAFF_SERVICE_KEY,
            STAFF_SERVICE.IS_ACTIVE,
            STAFF_SERVICE.CREATED_AT,
            STAFF_SERVICE.UPDATED_AT
        )
            .from(STAFF_SERVICE)
            .where(condition)
            .orderBy(STAFF_SERVICE.SERVICE_ID.asc())
            .fetch()
            .map {
                StaffService(
                    id = it.get(STAFF_SERVICE.ID)!!,
                    staffId = it.get(STAFF_SERVICE.STAFF_ID)!!,
                    serviceId = it.get(STAFF_SERVICE.SERVICE_ID)!!,
                    staffServiceKey = it.get(STAFF_SERVICE.STAFF_SERVICE_KEY),
                    isActive = it.get(STAFF_SERVICE.IS_ACTIVE)!!,
                    createdAt = it.get(STAFF_SERVICE.CREATED_AT)!!,
                    updatedAt = it.get(STAFF_SERVICE.UPDATED_AT)!!
                )
            }
    }
}
