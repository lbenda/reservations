package cz.lbenda.reservation.tenant

import java.time.OffsetDateTime
import java.util.UUID

data class Business(
    val id: UUID,
    val slug: String,
    val name: String,
    val timezone: String,
    val currency: String,
    val status: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewBusiness(
    val id: UUID,
    val slug: String,
    val name: String,
    val timezone: String,
    val currency: String,
    val status: String
)

data class BusinessUpdate(
    val slug: String,
    val name: String,
    val timezone: String,
    val currency: String,
    val status: String
)
