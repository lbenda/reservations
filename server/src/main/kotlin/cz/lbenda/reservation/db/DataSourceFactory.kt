package cz.lbenda.reservation.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DataSourceFactory {
    fun create(settings: DbSettings): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = settings.url
            username = settings.user
            password = settings.password
            maximumPoolSize = settings.maxPoolSize
            connectionTimeout = settings.connectionTimeout.toMillis()
        }
        return HikariDataSource(config)
    }
}
