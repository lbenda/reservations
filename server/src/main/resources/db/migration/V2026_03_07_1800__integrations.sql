-- Integrations module

create table webhook_endpoint (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    endpoint_key varchar(64) not null unique,
    url varchar(2048) not null,
    is_active boolean not null,
    secret_ref varchar(128),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_webhook_business_id on webhook_endpoint(business_id);

create table webhook_delivery (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    webhook_endpoint_id uuid not null references webhook_endpoint(id) on delete cascade,
    event_id uuid not null references audit_event(id) on delete cascade,
    delivery_key varchar(128) not null unique,
    status varchar(32) not null,
    attempt_count integer not null,
    last_attempt_at timestamptz,
    response_code integer,
    created_at timestamptz not null default now()
);

create index idx_webhook_delivery_business_id on webhook_delivery(business_id);
create index idx_webhook_delivery_endpoint_id on webhook_delivery(webhook_endpoint_id);
create index idx_webhook_delivery_event_id on webhook_delivery(event_id);

create table external_calendar (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    staff_id uuid not null references staff(id) on delete cascade,
    provider varchar(64) not null,
    provider_account_id varchar(128) not null,
    sync_enabled boolean not null,
    last_synced_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_external_calendar_business_id on external_calendar(business_id);
create index idx_external_calendar_staff_id on external_calendar(staff_id);
create unique index uq_external_calendar_provider_account on external_calendar(provider, provider_account_id);

create table busy_block (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    staff_id uuid not null references staff(id) on delete cascade,
    external_calendar_id uuid not null references external_calendar(id) on delete cascade,
    provider_event_id varchar(128) not null,
    start_at timestamptz not null,
    end_at timestamptz not null,
    summary varchar(160),
    created_at timestamptz not null default now(),
    unique (external_calendar_id, provider_event_id)
);

create index idx_busy_block_business_id on busy_block(business_id);
create index idx_busy_block_staff_id on busy_block(staff_id);
create index idx_busy_block_external_calendar_id on busy_block(external_calendar_id);
