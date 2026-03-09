package cz.lbenda.reservation.db

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource

object JooqContextFactory {
    fun create(dataSource: DataSource): DSLContext =
        DSL.using(dataSource, SQLDialect.POSTGRES)
}
