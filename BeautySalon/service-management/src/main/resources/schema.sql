set search_path = salon;
-- create table clients;
-- use clients;
create schema IF NOT EXISTS salon;

create table if not exists services
(
    id          SERIAL primary key,
    title       VARCHAR(50)  NOT NULL,
    price       DECIMAL      NOT NULL,
    duration    INT          NOT NULL,
    description VARCHAR(255) NOT NULL,
    consumables text[]
);

create table if not exists service_to_consumable
(
    id            SERIAL primary key,
    service_id    SERIAL NOT NULL,
    consumable_id SERIAL NOT NULL
);

-- drop table services;
-- drop schema services;
-- select *from consumables;


-- alter table service_to_consumable
--     add constraint service_to_consumable_consumable__fk
--         foreign key (consumable_id) references consumables (id);
--         foreign key () references salon.consumables ();

-- alter table service_to_consumable
--     add constraint service_to_consumable_services_id_fk
--         foreign key (service_id) references services;

create table if not exists consumables
(
    id       integer primary key   not null default nextval('consumables_id_seq'::regclass),
    title    character varying(50) not null,
    measure  character varying(50)          default 'PIECE',
    price    numeric,
    services text[]
);

