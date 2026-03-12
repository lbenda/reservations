package cz.lbenda.reservation.catalog

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class StaffRepository(private val dsl: DSLContext) {
    fun create(newStaff: NewStaff): Staff {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.insertInto(STAFF)
            .set(STAFF.ID, newStaff.id)
            .set(STAFF.BUSINESS_ID, newStaff.businessId)
            .set(STAFF.LOCATION_ID, newStaff.locationId)
            .set(STAFF.DISPLAY_NAME, newStaff.displayName)
            .set(STAFF.EMAIL, newStaff.email)
            .set(STAFF.PHONE, newStaff.phone)
            .set(STAFF.BIO, newStaff.bio)
            .set(STAFF.STATUS, newStaff.status)
            .set(STAFF.CREATED_AT, now)
            .set(STAFF.UPDATED_AT, now)
            .returning(
                STAFF.ID,
                STAFF.BUSINESS_ID,
                STAFF.LOCATION_ID,
                STAFF.DISPLAY_NAME,
                STAFF.EMAIL,
                STAFF.PHONE,
                STAFF.BIO,
                STAFF.STATUS,
                STAFF.CREATED_AT,
                STAFF.UPDATED_AT
            )
            .fetchOne() ?: error("Failed to insert staff")

        return Staff(
            id = record.get(STAFF.ID)!!,
            businessId = record.get(STAFF.BUSINESS_ID)!!,
            locationId = record.get(STAFF.LOCATION_ID)!!,
            displayName = record.get(STAFF.DISPLAY_NAME)!!,
            email = record.get(STAFF.EMAIL),
            phone = record.get(STAFF.PHONE),
            bio = record.get(STAFF.BIO),
            status = record.get(STAFF.STATUS)!!,
            createdAt = record.get(STAFF.CREATED_AT)!!,
            updatedAt = record.get(STAFF.UPDATED_AT)!!
        )
    }

    fun findById(id: UUID): Staff? {
        val record = selectBase()
            .from(STAFF)
            .where(STAFF.ID.eq(id))
            .fetchOne() ?: return null

        return toStaff(record)
    }

    fun findById(businessId: UUID, id: UUID): Staff? {
        val record = selectBase()
            .from(STAFF)
            .where(STAFF.ID.eq(id).and(STAFF.BUSINESS_ID.eq(businessId)))
            .fetchOne() ?: return null

        return toStaff(record)
    }

    fun listByBusiness(businessId: UUID, status: String? = null): List<Staff> {
        var condition: org.jooq.Condition = STAFF.BUSINESS_ID.eq(businessId)
        if (status != null) {
            condition = condition.and(STAFF.STATUS.eq(status))
        }

        return selectBase()
            .from(STAFF)
            .where(condition)
            .orderBy(STAFF.DISPLAY_NAME.asc())
            .fetch()
            .map(::toStaff)
    }

    fun update(id: UUID, update: StaffUpdate): Staff? {
        return update(null, id, update)
    }

    fun update(businessId: UUID?, id: UUID, update: StaffUpdate): Staff? {
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val record = dsl.update(STAFF)
            .set(STAFF.DISPLAY_NAME, update.displayName)
            .set(STAFF.EMAIL, update.email)
            .set(STAFF.PHONE, update.phone)
            .set(STAFF.BIO, update.bio)
            .set(STAFF.STATUS, update.status)
            .set(STAFF.UPDATED_AT, now)
            .where(
                if (businessId == null) {
                    STAFF.ID.eq(id)
                } else {
                    STAFF.ID.eq(id).and(STAFF.BUSINESS_ID.eq(businessId))
                }
            )
            .returning(
                STAFF.ID,
                STAFF.BUSINESS_ID,
                STAFF.LOCATION_ID,
                STAFF.DISPLAY_NAME,
                STAFF.EMAIL,
                STAFF.PHONE,
                STAFF.BIO,
                STAFF.STATUS,
                STAFF.CREATED_AT,
                STAFF.UPDATED_AT
            )
            .fetchOne() ?: return null

        return toStaff(record)
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(STAFF)
            .where(STAFF.ID.eq(id))
            .execute() > 0

    private fun selectBase() = dsl.select(
        STAFF.ID,
        STAFF.BUSINESS_ID,
        STAFF.LOCATION_ID,
        STAFF.DISPLAY_NAME,
        STAFF.EMAIL,
        STAFF.PHONE,
        STAFF.BIO,
        STAFF.STATUS,
        STAFF.CREATED_AT,
        STAFF.UPDATED_AT
    )

    private fun toStaff(record: org.jooq.Record): Staff =
        Staff(
            id = record.get(STAFF.ID)!!,
            businessId = record.get(STAFF.BUSINESS_ID)!!,
            locationId = record.get(STAFF.LOCATION_ID)!!,
            displayName = record.get(STAFF.DISPLAY_NAME)!!,
            email = record.get(STAFF.EMAIL),
            phone = record.get(STAFF.PHONE),
            bio = record.get(STAFF.BIO),
            status = record.get(STAFF.STATUS)!!,
            createdAt = record.get(STAFF.CREATED_AT)!!,
            updatedAt = record.get(STAFF.UPDATED_AT)!!
        )
}
