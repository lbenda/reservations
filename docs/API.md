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
