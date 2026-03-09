package cz.lbenda.reservation.db

import java.time.Duration

data class DbSettings(
    val url: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int = 5,
    val connectionTimeout: Duration = Duration.ofSeconds(5)
)
