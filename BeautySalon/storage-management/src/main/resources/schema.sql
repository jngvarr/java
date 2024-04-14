-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS consumables;

create table if not exists consumables
(
    id      SERIAL primary key,
    title   VARCHAR(50) NOT NULL,
    measure VARCHAR(50) DEFAULT 'PIECE',
    price   DECIMAL,
    services text[]
);

-- drop schema consumables;
-- drop table consumables;

-- set search_path = salon;
--
-- create schema IF NOT EXISTS salon;
--
-- create table salon.services
-- (
--     id          bigint generated always as identity primary key,
--     title       text    not null,
--     price       numeric not null,
--     duration    integer not null,
--     description text    not null
-- );
--
-- create table if not exists consumables
-- (
--     id      bigint generated always as identity primary key,
--     title   text NOT NULL,
--     measure text DEFAULT 'PIECE',
--     price   DECIMAL
-- );
--
-- create table service_to_consumables
-- (
--     id            bigint generated always as identity,
--     consumable_id bigint not null
--         constraint service_to_consumables_fk1
--             references consumables (id),
--     service_id    bigint not null
--         constraint service_to_consumables_fk2
--             references services (id)
-- );
--
-- create table if not exists clients
-- (
--     id         bigint generated always as identity primary key,
--     first_name TEXT NOT NULL,
--     last_name  TEXT,
--     contact    TEXT NOT NULL,
--     dob        DATE
-- );
--
-- create table if not exists visits
-- (
--     id         bigint generated always as identity primary key,
--     date       DATE   NOT NULL,
--     start_time TIME   NOT NULL,
--     service_id bigint not null,
--     client_id  bigint not null,
--     master_id  bigint not null
-- );
--
-- create table if not exists employees
-- (
--     id         bigint generated always as identity primary key,
--     first_name TEXT NOT NULL,
--     last_name  TEXT NOT NULL,
--     contact    TEXT NOT NULL,
--     dob        DATE NOT NULL,
--     function   TEXT NOT NULL
-- );
--
-- insert into service_to_consumables (service_id, consumable_id)
-- select s.id, c.id
-- from services s,
--      consumables c
-- where s.title = 'Простое окрашивание'
--   and c.title in ('Краска',
--                   'Полотенце',
--                   'Оксидант1',
--                   'Оксидант2',
--                   'Оксидант3'
--     )
