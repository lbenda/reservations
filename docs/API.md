# API

## Service Catalog

### Admin endpoints

#### `GET /api/admin/services`
- Header: `X-Business-Id: <uuid>`
- Query:
  - `isActive=true|false` optional
- Returns services for the business, sorted by `name`.

#### `POST /api/admin/services`
- Header: `X-Business-Id: <uuid>`
- Creates a service for the business.

#### `GET /api/admin/services/{id}`
- Header: `X-Business-Id: <uuid>`
- Returns one service if it belongs to the business.

#### `PATCH /api/admin/services/{id}`
- Header: `X-Business-Id: <uuid>`
- Replaces editable fields of the service.

#### `POST /api/admin/services/{id}/archive`
- Header: `X-Business-Id: <uuid>`
- Sets `isActive=false`.

### Public endpoints

#### `GET /api/public/services`
- Query:
  - `businessId=<uuid>` required
- Returns active services only.

### Service payload

```json
{
  "serviceCode": "SVC-100",
  "name": "Massage 60",
  "description": "Relaxing massage",
  "durationMinutes": 60,
  "bufferBeforeMinutes": 5,
  "bufferAfterMinutes": 10,
  "minAdvanceMinutes": 120,
  "maxAdvanceDays": 30,
  "cancellationPolicy": "24 hours notice",
  "priceAmount": "1200.00",
  "priceCurrency": "CZK",
  "isActive": true
}
```

### Validation errors

Invalid payloads return `400`:

```json
{
  "message": "Validation failed",
  "errors": [
    {
      "field": "durationMinutes",
      "message": "must be greater than 0"
    }
  ]
}
```

## Staff Management

### Admin endpoints

#### `GET /api/admin/staff`
- Header: `X-Business-Id: <uuid>`
- Query:
  - `status=active|inactive` optional
- Returns staff for the business, sorted by `displayName`.

#### `POST /api/admin/staff`
- Header: `X-Business-Id: <uuid>`
- Creates a staff member for the business.

#### `GET /api/admin/staff/{id}`
- Header: `X-Business-Id: <uuid>`
- Returns one staff member if it belongs to the business.

#### `PATCH /api/admin/staff/{id}`
- Header: `X-Business-Id: <uuid>`
- Replaces editable fields of the staff member.

#### `GET /api/admin/staff/{id}/weekly-schedules`
- Header: `X-Business-Id: <uuid>`
- Returns weekly schedule ranges for the staff member.

#### `POST /api/admin/staff/{id}/weekly-schedules`
- Header: `X-Business-Id: <uuid>`
- Creates one weekly schedule range for the staff member.

#### `PATCH /api/admin/staff/{id}/weekly-schedules/{scheduleId}`
- Header: `X-Business-Id: <uuid>`
- Updates one weekly schedule range.

#### `DELETE /api/admin/staff/{id}/weekly-schedules/{scheduleId}`
- Header: `X-Business-Id: <uuid>`
- Deletes one weekly schedule range.

#### `GET /api/admin/staff/{id}/schedule-exceptions`
- Header: `X-Business-Id: <uuid>`
- Returns schedule exceptions for the staff member.

#### `POST /api/admin/staff/{id}/schedule-exceptions`
- Header: `X-Business-Id: <uuid>`
- Creates one schedule exception.

#### `PATCH /api/admin/staff/{id}/schedule-exceptions/{exceptionId}`
- Header: `X-Business-Id: <uuid>`
- Updates one schedule exception.

#### `DELETE /api/admin/staff/{id}/schedule-exceptions/{exceptionId}`
- Header: `X-Business-Id: <uuid>`
- Deletes one schedule exception.

### Staff payload

```json
{
  "locationId": "00000000-0000-0000-0000-000000000112",
  "displayName": "Eva Staff",
  "email": "eva@acme.test",
  "phone": "+420123456789",
  "bio": "Senior therapist",
  "status": "active"
}
```

### Weekly schedule payload

```json
{
  "dayOfWeek": 1,
  "rangeType": "WORK",
  "startTime": "09:00",
  "endTime": "17:00"
}
```

### Schedule exception payload

```json
{
  "exceptionDate": "2026-03-20",
  "rangeType": "DAY_OFF",
  "startTime": null,
  "endTime": null,
  "note": "Vacation"
}
```

## Availability

### Public endpoint

#### `GET /api/public/availability`
- Query:
  - `businessId=<uuid>` required
  - `serviceId=<uuid>` required
  - `startDate=YYYY-MM-DD` required
  - `endDate=YYYY-MM-DD` required
  - `timezone=<iana-zone>` required
  - `slotIntervalMinutes=<int>` required
  - `staffId=<uuid>` optional
- Returns available slots for the requested service and date range.

### Availability response

```json
[
  {
    "startAt": "2026-03-23T09:00Z",
    "endAt": "2026-03-23T09:30Z",
    "staffId": "00000000-0000-0000-0000-000000000201"
  }
]
```
