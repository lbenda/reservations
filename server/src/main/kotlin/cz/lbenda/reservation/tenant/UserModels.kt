package cz.lbenda.reservation.tenant

import java.time.OffsetDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val locale: String?,
    val status: String,
    val lastLoginAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewUser(
    val id: UUID,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val locale: String?,
    val status: String
)

data class UserUpdate(
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val locale: String?,
    val status: String,
    val lastLoginAt: OffsetDateTime?
)
