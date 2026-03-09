package cz.lbenda.reservation.db

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.testcontainers.containers.PostgreSQLContainer
import java.time.Duration
import javax.sql.DataSource

object TestDatabase {
    private val externalSettings: DbSettings? = readExternalSettings()
    private val container: PostgreSQLContainer<*>? = if (externalSettings == null) {
        PostgreSQLContainer("postgres:16-alpine").apply {
            withDatabaseName("reservation_test")
            withUsername("reservation")
            withPassword("reservation")
            withStartupTimeout(Duration.ofSeconds(60))
            start()
        }
    } else {
        null
    }

    private val settings: DbSettings = externalSettings ?: DbSettings(
        url = container!!.jdbcUrl,
        user = container.username,
        password = container.password,
        maxPoolSize = 4,
        connectionTimeout = Duration.ofSeconds(5)
    )

    val dataSource: DataSource = DataSourceFactory.create(settings)
    val dsl: DSLContext = JooqContextFactory.create(dataSource)

    init {
        FlywayMigrator.migrate(dataSource)
    }

    fun resetTenantAccess() {
        dsl.execute(
            """
            truncate table
                booking,
                block,
                busy_block,
                external_calendar,
                webhook_delivery,
                webhook_endpoint,
                audit_event,
                entitlement_ledger,
                package,
                payment,
                consent,
                client,
                staff_service,
                staff,
                service,
                business_user,
                api_key,
                location,
                app_user,
                role,
                business
            restart identity cascade
            """.trimIndent()
        )
    }

    fun close() {
        if (dataSource is HikariDataSource) {
            dataSource.close()
        }
        container?.stop()
    }

    private fun readExternalSettings(): DbSettings? {
        val url = System.getenv("TEST_DB_URL")
        val user = System.getenv("TEST_DB_USER")
        val password = System.getenv("TEST_DB_PASS")

        return if (url.isNullOrBlank() || user.isNullOrBlank() || password.isNullOrBlank()) {
            null
        } else {
            DbSettings(
                url = url,
                user = user,
                password = password,
                maxPoolSize = 4,
                connectionTimeout = Duration.ofSeconds(5)
            )
        }
    }
}
