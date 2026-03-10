# LDM — Reservation Application

This Logical Data Model (LDM) describes the core domain entities and their relationships for the reservation application. It is derived from `docs/data-model.puml` and focuses on keys (PK/UK) and relationships (FK) across the domain.

## Domain Overview
The system serves multiple businesses (tenants). Each business has locations, staff, services, clients, and bookings. Bookings can be paid, can consume packages (entitlements), and are audited. Integrations are supported via API keys, webhooks, and external calendars.

## Module Boundaries
The LDM can be modularized into cohesive domains that can evolve with minimal cross-impact:
| Module | Entities |
| --- | --- |
| Tenant & Access | Business, Location, User, Role, BusinessUser, ApiKey |
| Catalog & Staff | Service, Staff, StaffService |
| Clients | Client, Consent |
| Booking | Booking, Block |
| Payments | Payment |
| Packages & Entitlements | Package, EntitlementLedger |
| Audit | AuditEvent |
| Integrations | WebhookEndpoint, WebhookDelivery, ExternalCalendar, BusyBlock |

## Terminology
| Term | Meaning |
| --- | --- |
| Business | Tenant of the application. Owner of data and configuration. |
| Location | Physical branch/site of a business. |
| User | Internal system user (not a client). |
| Role | User permission level within a business. |
| BusinessUser | Membership of a user in a specific business. |
| Staff | Service provider (employee). |
| Service | Offered service (e.g., “Massage 60 min”). |
| StaffService | Capability/assignment of staff to provide a service. |
| Client | Customer of a business who books services. |
| Booking | Reservation of a service with a specific staff member at a location. |
| Block | Time block (unavailable slot). |
| Payment | Payment for a booking. |
| Package | Prepaid package or service bundle. |
| EntitlementLedger | Ledger of client entitlements and redemptions. |
| Consent | Client consent (e.g., marketing, terms). |
| AuditEvent | Audit trail record of system actions. |
| ApiKey | Integration API key (identifier without the secret). |
| WebhookEndpoint | Webhook endpoint registered by an integration. |
| WebhookDelivery | Individual webhook delivery attempt. |
| ExternalCalendar | External calendar linked to staff. |
| BusyBlock | Imported busy time from an external calendar. |

## Entities and Meaning
### Business
Core tenant. Owns all other entities within the business.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| slug | slug | varchar(64) | yes | Public unique identifier for the business. |
| name | name | varchar(160) | yes | Display name of the business. |
| timezone | timezone | varchar(64) | yes | IANA timezone for scheduling (e.g., `Europe/Prague`). |
| currency | currency | char(3) | yes | ISO 4217 currency code. |
| status | status | varchar(32) | yes | Business status (e.g., active, paused). |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Location
Physical site. `slug` is unique within a Business.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| slug | slug | varchar(64) | yes | Unique within Business. |
| name | name | varchar(160) | yes | Location display name. |
| addressLine1 | address_line1 | varchar(160) | yes | Street address. |
| addressLine2 | address_line2 | varchar(160) | no | Additional address line. |
| city | city | varchar(96) | yes | City. |
| postalCode | postal_code | varchar(24) | yes | Postal/ZIP code. |
| countryCode | country_code | char(2) | yes | ISO 3166-1 alpha-2. |
| phone | phone | varchar(32) | no | Public contact phone. |
| email | email | varchar(160) | no | Public contact email. |
| timezone | timezone | varchar(64) | no | Overrides business timezone if needed. |
| status | status | varchar(32) | yes | Location status. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### User
Internal system user, uniquely identified by `email`.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| email | email | varchar(254) | yes | Unique login email. |
| firstName | first_name | varchar(80) | yes | Given name. |
| lastName | last_name | varchar(80) | yes | Family name. |
| phone | phone | varchar(32) | no | Optional contact phone. |
| locale | locale | varchar(16) | no | UI locale (e.g., `cs-CZ`). |
| status | status | varchar(32) | yes | User status. |
| lastLoginAt | last_login_at | timestamptz | no | Last login timestamp. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Role
User role in a business (`OWNER`, `MANAGER`, `STAFF`).

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| code | code | varchar(32) | yes | Unique role code. |
| name | name | varchar(80) | yes | Human-readable role name. |
| description | description | text | no | Role description. |

### BusinessUser
Join entity User–Business with an assigned Role. May have its own business key.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| userId | user_id | uuid | yes | FK to User. |
| roleId | role_id | uuid | yes | FK to Role. |
| businessUserKey | business_user_key | varchar(64) | no | Optional business key (invite/code). |
| status | status | varchar(32) | yes | Membership status. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Staff
Employee/service provider. Belongs to a Business and a Location.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| locationId | location_id | uuid | yes | FK to Location. |
| displayName | display_name | varchar(160) | yes | Name shown to clients. |
| email | email | varchar(254) | no | Optional contact email. |
| phone | phone | varchar(32) | no | Optional contact phone. |
| bio | bio | text | no | Short bio or description. |
| status | status | varchar(32) | yes | Staff status. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Service
Service offered by the business. Optional internal business code.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| serviceCode | service_code | varchar(64) | no | Optional internal code. |
| name | name | varchar(160) | yes | Service name. |
| description | description | text | no | Service description. |
| durationMinutes | duration_minutes | integer | yes | Default duration in minutes. |
| bufferBeforeMinutes | buffer_before_minutes | integer | no | Setup buffer before booking. |
| bufferAfterMinutes | buffer_after_minutes | integer | no | Cleanup buffer after booking. |
| minAdvanceMinutes | min_advance_minutes | integer | no | Minimum lead time before booking. |
| maxAdvanceDays | max_advance_days | integer | no | Maximum days ahead for booking. |
| cancellationPolicy | cancellation_policy | text | no | Customer-facing cancellation policy. |
| priceAmount | price_amount | numeric(12,2) | yes | Base price amount. |
| priceCurrency | price_currency | char(3) | yes | ISO 4217 currency code. |
| isActive | is_active | boolean | yes | Service availability flag. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### StaffService
M:N link between Staff and Service (who can provide what). May have its own business key or enforce uniqueness by (staffId, serviceId).

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| staffId | staff_id | uuid | yes | FK to Staff. |
| serviceId | service_id | uuid | yes | FK to Service. |
| staffServiceKey | staff_service_key | varchar(64) | no | Optional business key. |
| isActive | is_active | boolean | yes | Assignment active flag. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Client
Customer of the business. Identified by unique email/phone.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| email | email | varchar(254) | no | Client email (unique if present). |
| phone | phone | varchar(32) | no | Client phone (unique if present). |
| firstName | first_name | varchar(80) | yes | Given name. |
| lastName | last_name | varchar(80) | yes | Family name. |
| locale | locale | varchar(16) | no | Preferred locale. |
| notes | notes | text | no | Internal notes. |
| status | status | varchar(32) | yes | Client status. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Booking
Reservation linking Business, Location, Service, Staff, and Client. Has a human-friendly `publicRef`.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| locationId | location_id | uuid | yes | FK to Location. |
| serviceId | service_id | uuid | yes | FK to Service. |
| staffId | staff_id | uuid | yes | FK to Staff. |
| clientId | client_id | uuid | yes | FK to Client. |
| publicRef | public_ref | varchar(32) | yes | Human-friendly booking reference. |
| status | status | varchar(32) | yes | Booking status (e.g., reserved, confirmed, canceled). |
| startAt | start_at | timestamptz | yes | Start timestamp. |
| endAt | end_at | timestamptz | yes | End timestamp. |
| timezone | timezone | varchar(64) | yes | Effective timezone for the booking. |
| priceAmount | price_amount | numeric(12,2) | yes | Final price charged. |
| priceCurrency | price_currency | char(3) | yes | ISO 4217 currency code. |
| notes | notes | text | no | Internal notes. |
| clientMessage | client_message | text | no | Client-provided note. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Block
Time block. Tied to Business and Location; optionally Staff.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| locationId | location_id | uuid | yes | FK to Location. |
| staffId | staff_id | uuid | no | FK to Staff (nullable if location-wide). |
| startAt | start_at | timestamptz | yes | Block start timestamp. |
| endAt | end_at | timestamptz | yes | Block end timestamp. |
| reason | reason | varchar(160) | no | Optional reason label. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Payment
Payment for a Booking with external idempotent reference `providerRef`.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| bookingId | booking_id | uuid | yes | FK to Booking. |
| providerRef | provider_ref | varchar(128) | yes | External idempotency reference. |
| providerName | provider_name | varchar(64) | yes | Payment provider name. |
| amount | amount | numeric(12,2) | yes | Paid amount. |
| currency | currency | char(3) | yes | ISO 4217 currency code. |
| status | status | varchar(32) | yes | Payment status. |
| paidAt | paid_at | timestamptz | no | Actual payment timestamp. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### Package
Package/bundle within a Business.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| packageCode | package_code | varchar(64) | no | Optional business code. |
| name | name | varchar(160) | yes | Package name. |
| description | description | text | no | Package description. |
| totalCredits | total_credits | integer | yes | Total credits/uses included. |
| validityDays | validity_days | integer | no | Validity period in days. |
| priceAmount | price_amount | numeric(12,2) | yes | Package price. |
| priceCurrency | price_currency | char(3) | yes | ISO 4217 currency code. |
| isActive | is_active | boolean | yes | Package availability flag. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### EntitlementLedger
Ledger of client entitlements (purchase and redemption). Linked to Package and Client; optionally Booking on redemption.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| clientId | client_id | uuid | yes | FK to Client. |
| packageId | package_id | uuid | yes | FK to Package. |
| bookingId | booking_id | uuid | no | FK to Booking when redeemed. |
| entryType | entry_type | varchar(32) | yes | Ledger entry type (purchase, redemption, adjustment). |
| quantity | quantity | integer | yes | Credits added or consumed. |
| effectiveAt | effective_at | timestamptz | yes | Effective timestamp. |
| expiresAt | expires_at | timestamptz | no | Expiration timestamp if applicable. |
| note | note | text | no | Optional note. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |

### Consent
Client consents (e.g., marketing, terms-v1) with `consentKey`.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| clientId | client_id | uuid | yes | FK to Client. |
| consentKey | consent_key | varchar(64) | yes | Consent key (e.g., marketing, terms-v1). |
| granted | granted | boolean | yes | Whether consent is granted. |
| grantedAt | granted_at | timestamptz | no | When consent was granted. |
| revokedAt | revoked_at | timestamptz | no | When consent was revoked. |
| source | source | varchar(64) | no | Source of consent (web, admin, import). |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |

### AuditEvent
Audit trail of user actions on Booking/Client.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| actorUserId | actor_user_id | uuid | yes | FK to User. |
| bookingId | booking_id | uuid | no | FK to Booking (nullable). |
| clientId | client_id | uuid | no | FK to Client (nullable). |
| eventType | event_type | varchar(64) | yes | Type of event (e.g., booking_created). |
| payload | payload | jsonb | no | Event payload for audit context. |
| occurredAt | occurred_at | timestamptz | yes | Event timestamp. |

### ApiKey
API keys for integrations. Stores `keyId`, never the raw secret.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| keyId | key_id | varchar(64) | yes | Public key identifier. |
| name | name | varchar(80) | yes | Display name for the key. |
| lastUsedAt | last_used_at | timestamptz | no | Last usage timestamp. |
| revokedAt | revoked_at | timestamptz | no | Revocation timestamp. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |

### WebhookEndpoint
Webhook endpoints registered by integrations.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| endpointKey | endpoint_key | varchar(64) | yes | Unique endpoint key. |
| url | url | varchar(2048) | yes | Target URL for deliveries. |
| isActive | is_active | boolean | yes | Endpoint enabled flag. |
| secretRef | secret_ref | varchar(128) | no | Reference to stored secret. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### WebhookDelivery
Individual delivery attempts with idempotent `deliveryKey`.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| webhookEndpointId | webhook_endpoint_id | uuid | yes | FK to WebhookEndpoint. |
| eventId | event_id | uuid | yes | FK to AuditEvent. |
| deliveryKey | delivery_key | varchar(128) | yes | Idempotency key. |
| status | status | varchar(32) | yes | Delivery status. |
| attemptCount | attempt_count | integer | yes | Number of attempts. |
| lastAttemptAt | last_attempt_at | timestamptz | no | Last attempt timestamp. |
| responseCode | response_code | integer | no | Last HTTP response code. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |

### ExternalCalendar
External calendar connected to Staff (e.g., Google Calendar).

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| staffId | staff_id | uuid | yes | FK to Staff. |
| provider | provider | varchar(64) | yes | Calendar provider (e.g., google). |
| providerAccountId | provider_account_id | varchar(128) | yes | Provider account identifier. |
| syncEnabled | sync_enabled | boolean | yes | Sync enabled flag. |
| lastSyncedAt | last_synced_at | timestamptz | no | Last successful sync. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |
| updatedAt | updated_at | timestamptz | yes | Last update timestamp. |

### BusyBlock
Busy time imported from external calendars.

| Kotlin Field | SQL Column | Type (SQL) | Required | Description |
| --- | --- | --- | --- | --- |
| id | id | uuid | yes | Primary key (UUIDv7). |
| businessId | business_id | uuid | yes | FK to Business. |
| staffId | staff_id | uuid | yes | FK to Staff. |
| externalCalendarId | external_calendar_id | uuid | yes | FK to ExternalCalendar. |
| providerEventId | provider_event_id | varchar(128) | yes | Provider event identifier. |
| startAt | start_at | timestamptz | yes | Busy start timestamp. |
| endAt | end_at | timestamptz | yes | Busy end timestamp. |
| summary | summary | varchar(160) | no | Event summary/title. |
| createdAt | created_at | timestamptz | yes | Creation timestamp. |

## Relationships (High-Level)

1. Business is the parent tenant for Location, Staff, Service, Client, Booking, Block, Payment, Package, EntitlementLedger, Consent, AuditEvent, ApiKey, WebhookEndpoint, WebhookDelivery, ExternalCalendar, BusyBlock, and BusinessUser.
2. Location hosts Staff and Booking and is the target for Block.
3. User is a member of Business via BusinessUser and has a Role.
4. Staff provides Service via StaffService.
5. Booking links Service, Staff, and Client and may have Payment.
6. Package → EntitlementLedger → Client represents entitlements and redemption.
7. AuditEvent records actions by User on Booking/Client.
8. WebhookEndpoint → WebhookDelivery is linked to AuditEvent as the event source.
9. ExternalCalendar → BusyBlock syncs staff availability.

## Keys and Identity
1. All entities use `id : uuid7` as the primary key.
2. Business, User, Role, ApiKey, WebhookEndpoint, and others have unique business keys for human/integration usage.
3. Location `slug` is unique within a Business (enforced via composite uniqueness).
4. Booking `publicRef` is a human-friendly unique identifier.
5. Payment and WebhookDelivery use idempotent keys (`providerRef`, `deliveryKey`).

## Design Notes
1. The model is multi-tenant (everything is scoped to Business).
2. Integrations are handled via API keys, webhooks, and external calendars.
3. EntitlementLedger is a ledger of transactions, not just a current balance.
