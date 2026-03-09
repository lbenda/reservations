-- Catalog & Staff module

create table staff (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    location_id uuid not null references location(id) on delete restrict,
    display_name varchar(160) not null,
    email varchar(254),
    phone varchar(32),
    bio text,
    status varchar(32) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_staff_business_id on staff(business_id);
create index idx_staff_location_id on staff(location_id);

create table service (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    service_code varchar(64),
    name varchar(160) not null,
    description text,
    duration_minutes integer not null,
    buffer_before_minutes integer,
    buffer_after_minutes integer,
    price_amount numeric(12,2) not null,
    price_currency char(3) not null,
    is_active boolean not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_service_business_id on service(business_id);
create unique index uq_service_code on service(service_code);

create table staff_service (
    id uuid primary key,
    staff_id uuid not null references staff(id) on delete cascade,
    service_id uuid not null references service(id) on delete cascade,
    staff_service_key varchar(64),
    is_active boolean not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (staff_id, service_id)
);

create unique index uq_staff_service_key on staff_service(staff_service_key);
