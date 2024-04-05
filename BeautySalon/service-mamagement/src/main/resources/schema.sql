-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS services;

create table if not exists services
(
    id          SERIAL primary key,
    title       VARCHAR(50)  NOT NULL,
    price       DECIMAL      NOT NULL,
    duration    INT          NOT NULL,
    description VARCHAR(255) NOT NULL,
    consumables text[]
);

create table if not exists service_consumable
(
    id            SERIAL primary key,
    service_id    int NOT NULL,
    consumable_id int NOT NULL
);

--  drop table services;
drop schema services;