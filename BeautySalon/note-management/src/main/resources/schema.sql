-- create table clients;
-- use clients;
-- CREATE SCHEMA IF NOT EXISTS services;

create table if not exists visits
(
    id         SERIAL primary key,
    date       DATE NOT NULL,
    start_time TIME NOT NULL,
    service_id INT  NOT NULL,
    client_id  INT  NOT NULL,
    master_id  INT  NOT NULL
);

--  drop table services;