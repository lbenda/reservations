-- Packages & Entitlements module

create table package (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    package_code varchar(64),
    name varchar(160) not null,
    description text,
    total_credits integer not null,
    validity_days integer,
    price_amount numeric(12,2) not null,
    price_currency char(3) not null,
    is_active boolean not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_package_business_id on package(business_id);
create unique index uq_package_code on package(package_code);

create table entitlement_ledger (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    client_id uuid not null references client(id) on delete cascade,
    package_id uuid not null references package(id) on delete cascade,
    booking_id uuid,
    entry_type varchar(32) not null,
    quantity integer not null,
    effective_at timestamptz not null,
    expires_at timestamptz,
    note text,
    created_at timestamptz not null default now()
);

create index idx_entitlement_business_id on entitlement_ledger(business_id);
create index idx_entitlement_client_id on entitlement_ledger(client_id);
create index idx_entitlement_package_id on entitlement_ledger(package_id);
create index idx_entitlement_booking_id on entitlement_ledger(booking_id);
