package cz.lbenda.reservation.tenant

import java.time.OffsetDateTime
import java.util.UUID

data class Location(
    val id: UUID,
    val businessId: UUID,
    val slug: String,
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val postalCode: String,
    val countryCode: String,
    val phone: String?,
    val email: String?,
    val timezone: String?,
    val status: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewLocation(
    val id: UUID,
    val businessId: UUID,
    val slug: String,
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val postalCode: String,
    val countryCode: String,
    val phone: String?,
    val email: String?,
    val timezone: String?,
    val status: String
)

data class LocationUpdate(
    val slug: String,
    val name: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val postalCode: String,
    val countryCode: String,
    val phone: String?,
    val email: String?,
    val timezone: String?,
    val status: String
)
