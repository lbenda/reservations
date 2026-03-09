package cz.lbenda.reservation.db

import org.flywaydb.core.Flyway
import javax.sql.DataSource

object FlywayMigrator {
    fun migrate(dataSource: DataSource) {
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()
    }
}
