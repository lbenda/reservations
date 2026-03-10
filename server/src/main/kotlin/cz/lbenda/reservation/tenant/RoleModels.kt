package cz.lbenda.reservation.tenant

import java.util.UUID

data class Role(
    val id: UUID,
    val code: String,
    val name: String,
    val description: String?
)

data class NewRole(
    val id: UUID,
    val code: String,
    val name: String,
    val description: String?
)

data class RoleUpdate(
    val code: String,
    val name: String,
    val description: String?
)
