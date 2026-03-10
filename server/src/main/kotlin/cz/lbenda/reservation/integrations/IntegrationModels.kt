package cz.lbenda.reservation.integrations

import java.time.OffsetDateTime
import java.util.UUID

data class WebhookEndpoint(
    val id: UUID,
    val businessId: UUID,
    val endpointKey: String,
    val url: String,
    val isActive: Boolean,
    val secretRef: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewWebhookEndpoint(
    val id: UUID,
    val businessId: UUID,
    val endpointKey: String,
    val url: String,
    val isActive: Boolean,
    val secretRef: String?
)

data class WebhookEndpointUpdate(
    val url: String,
    val isActive: Boolean,
    val secretRef: String?
)

data class WebhookDelivery(
    val id: UUID,
    val businessId: UUID,
    val webhookEndpointId: UUID,
    val eventId: UUID,
    val deliveryKey: String,
    val status: String,
    val attemptCount: Int,
    val lastAttemptAt: OffsetDateTime?,
    val responseCode: Int?,
    val createdAt: OffsetDateTime
)

data class NewWebhookDelivery(
    val id: UUID,
    val businessId: UUID,
    val webhookEndpointId: UUID,
    val eventId: UUID,
    val deliveryKey: String,
    val status: String,
    val attemptCount: Int,
    val lastAttemptAt: OffsetDateTime?,
    val responseCode: Int?
)

data class WebhookDeliveryUpdate(
    val status: String,
    val attemptCount: Int,
    val lastAttemptAt: OffsetDateTime?,
    val responseCode: Int?
)

data class ExternalCalendar(
    val id: UUID,
    val businessId: UUID,
    val staffId: UUID,
    val provider: String,
    val providerAccountId: String,
    val syncEnabled: Boolean,
    val lastSyncedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class NewExternalCalendar(
    val id: UUID,
    val businessId: UUID,
    val staffId: UUID,
    val provider: String,
    val providerAccountId: String,
    val syncEnabled: Boolean,
    val lastSyncedAt: OffsetDateTime?
)

data class ExternalCalendarUpdate(
    val provider: String,
    val providerAccountId: String,
    val syncEnabled: Boolean,
    val lastSyncedAt: OffsetDateTime?
)

data class BusyBlock(
    val id: UUID,
    val businessId: UUID,
    val staffId: UUID,
    val externalCalendarId: UUID,
    val providerEventId: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val summary: String?,
    val createdAt: OffsetDateTime
)

data class NewBusyBlock(
    val id: UUID,
    val businessId: UUID,
    val staffId: UUID,
    val externalCalendarId: UUID,
    val providerEventId: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val summary: String?
)

data class BusyBlockUpdate(
    val providerEventId: String,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime,
    val summary: String?
)
