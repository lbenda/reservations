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
    min_advance_minutes integer,
    max_advance_days integer,
    cancellation_policy text,
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

create table staff_weekly_schedule (
    id uuid primary key,
    staff_id uuid not null references staff(id) on delete cascade,
    day_of_week smallint not null,
    range_type varchar(16) not null,
    start_time time not null,
    end_time time not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint chk_staff_weekly_schedule_day_of_week check (day_of_week between 1 and 7),
    constraint chk_staff_weekly_schedule_range_type check (range_type in ('WORK', 'BREAK')),
    constraint chk_staff_weekly_schedule_time_order check (start_time < end_time)
);

create index idx_staff_weekly_schedule_staff_id on staff_weekly_schedule(staff_id);
create unique index uq_staff_weekly_schedule_slot
    on staff_weekly_schedule(staff_id, day_of_week, range_type, start_time, end_time);

create table staff_schedule_exception (
    id uuid primary key,
    staff_id uuid not null references staff(id) on delete cascade,
    exception_date date not null,
    range_type varchar(16) not null,
    start_time time,
    end_time time,
    note text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint chk_staff_schedule_exception_range_type check (range_type in ('WORK', 'BREAK', 'DAY_OFF')),
    constraint chk_staff_schedule_exception_time_order check (
        (range_type = 'DAY_OFF' and start_time is null and end_time is null) or
        (range_type <> 'DAY_OFF' and start_time is not null and end_time is not null and start_time < end_time)
    )
);

create index idx_staff_schedule_exception_staff_date on staff_schedule_exception(staff_id, exception_date);
