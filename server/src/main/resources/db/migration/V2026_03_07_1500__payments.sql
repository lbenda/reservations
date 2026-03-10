-- Payments module

create table payment (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    booking_id uuid not null,
    provider_ref varchar(128) not null unique,
    provider_name varchar(64) not null,
    amount numeric(12,2) not null,
    currency char(3) not null,
    status varchar(32) not null,
    paid_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_payment_business_id on payment(business_id);
create index idx_payment_booking_id on payment(booking_id);
