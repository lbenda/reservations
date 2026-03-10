-- Tenant & Access module

create table business (
    id uuid primary key,
    slug varchar(64) not null unique,
    name varchar(160) not null,
    timezone varchar(64) not null,
    currency char(3) not null,
    status varchar(32) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table location (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    slug varchar(64) not null,
    name varchar(160) not null,
    address_line1 varchar(160) not null,
    address_line2 varchar(160),
    city varchar(96) not null,
    postal_code varchar(24) not null,
    country_code char(2) not null,
    phone varchar(32),
    email varchar(160),
    timezone varchar(64),
    status varchar(32) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (business_id, slug)
);

create index idx_location_business_id on location(business_id);

create table app_user (
    id uuid primary key,
    email varchar(254) not null unique,
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    phone varchar(32),
    locale varchar(16),
    status varchar(32) not null,
    last_login_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table role (
    id uuid primary key,
    code varchar(32) not null unique,
    name varchar(80) not null,
    description text
);

create table business_user (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    user_id uuid not null references app_user(id) on delete cascade,
    role_id uuid not null references role(id),
    business_user_key varchar(64),
    status varchar(32) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index idx_business_user_business_id on business_user(business_id);
create index idx_business_user_user_id on business_user(user_id);
create index idx_business_user_role_id on business_user(role_id);
create unique index uq_business_user_key on business_user(business_user_key);

create table api_key (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    key_id varchar(64) not null unique,
    name varchar(80) not null,
    last_used_at timestamptz,
    revoked_at timestamptz,
    created_at timestamptz not null default now()
);

create index idx_api_key_business_id on api_key(business_id);
