-- Audit module

create table audit_event (
    id uuid primary key,
    business_id uuid not null references business(id) on delete cascade,
    actor_user_id uuid not null references app_user(id) on delete restrict,
    booking_id uuid,
    client_id uuid,
    event_type varchar(64) not null,
    payload jsonb,
    occurred_at timestamptz not null,
    created_at timestamptz not null default now()
);

create index idx_audit_business_id on audit_event(business_id);
create index idx_audit_actor_user_id on audit_event(actor_user_id);
create index idx_audit_booking_id on audit_event(booking_id);
create index idx_audit_client_id on audit_event(client_id);
create index idx_audit_event_type on audit_event(event_type);
