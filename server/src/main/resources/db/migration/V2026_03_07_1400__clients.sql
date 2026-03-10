-- Clients module

create table client (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    email varchar(254),
    phone varchar(32),
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    locale varchar(16),
    notes text,
    status varchar(32) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create unique index uq_client_email on client(email);
create unique index uq_client_phone on client(phone);
create index idx_client_business_id on client(business_id);

create table consent (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    client_id uuid not null references client(id) on delete cascade,
    consent_key varchar(64) not null,
    granted boolean not null,
    granted_at timestamptz,
    revoked_at timestamptz,
    source varchar(64),
    created_at timestamptz not null default now(),
    unique (client_id, consent_key)
);

create index idx_consent_business_id on consent(business_id);
create index idx_consent_client_id on consent(client_id);
