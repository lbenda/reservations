-- Booking module

create table booking (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    location_id uuid not null references location(id) on delete restrict,
    service_id uuid not null references service(id) on delete restrict,
    staff_id uuid not null references staff(id) on delete restrict,
    client_id uuid not null references client(id) on delete restrict,
    public_ref varchar(32) not null unique,
    status varchar(32) not null,
    start_at timestamptz not null,
    end_at timestamptz not null,
    timezone varchar(64) not null,
    price_amount numeric(12,2) not null,
    price_currency char(3) not null,
    notes text,
    client_message text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_booking_business_id on booking(business_id);
create index idx_booking_location_id on booking(location_id);
create index idx_booking_service_id on booking(service_id);
create index idx_booking_staff_id on booking(staff_id);
create index idx_booking_client_id on booking(client_id);

create table block (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    location_id uuid not null references location(id) on delete restrict,
    staff_id uuid references staff(id) on delete set null,
    start_at timestamptz not null,
    end_at timestamptz not null,
    reason varchar(160),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_block_business_id on block(business_id);
create index idx_block_location_id on block(location_id);
create index idx_block_staff_id on block(staff_id);
