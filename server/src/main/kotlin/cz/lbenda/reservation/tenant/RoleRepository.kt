package cz.lbenda.reservation.tenant

import org.jooq.DSLContext
import cz.lbenda.reservation.jooq.tables.references.*
import java.util.UUID

class RoleRepository(private val dsl: DSLContext) {
    fun create(newRole: NewRole): Role {
        val record = dsl.insertInto(ROLE)
            .set(ROLE.ID, newRole.id)
            .set(ROLE.CODE, newRole.code)
            .set(ROLE.NAME, newRole.name)
            .set(ROLE.DESCRIPTION, newRole.description)
            .returning(
                ROLE.ID,
                ROLE.CODE,
                ROLE.NAME,
                ROLE.DESCRIPTION
            )
            .fetchOne() ?: error("Failed to insert role")

        return Role(
            id = record.get(ROLE.ID)!!,
            code = record.get(ROLE.CODE)!!,
            name = record.get(ROLE.NAME)!!,
            description = record.get(ROLE.DESCRIPTION)
        )
    }

    fun findById(id: UUID): Role? {
        val record = dsl.select(
            ROLE.ID,
            ROLE.CODE,
            ROLE.NAME,
            ROLE.DESCRIPTION
        )
            .from(ROLE)
            .where(ROLE.ID.eq(id))
            .fetchOne() ?: return null

        return Role(
            id = record.get(ROLE.ID)!!,
            code = record.get(ROLE.CODE)!!,
            name = record.get(ROLE.NAME)!!,
            description = record.get(ROLE.DESCRIPTION)
        )
    }

    fun update(id: UUID, update: RoleUpdate): Role? {
        val record = dsl.update(ROLE)
            .set(ROLE.CODE, update.code)
            .set(ROLE.NAME, update.name)
            .set(ROLE.DESCRIPTION, update.description)
            .where(ROLE.ID.eq(id))
            .returning(
                ROLE.ID,
                ROLE.CODE,
                ROLE.NAME,
                ROLE.DESCRIPTION
            )
            .fetchOne() ?: return null

        return Role(
            id = record.get(ROLE.ID)!!,
            code = record.get(ROLE.CODE)!!,
            name = record.get(ROLE.NAME)!!,
            description = record.get(ROLE.DESCRIPTION)
        )
    }

    fun delete(id: UUID): Boolean =
        dsl.deleteFrom(ROLE)
            .where(ROLE.ID.eq(id))
            .execute() > 0
}
