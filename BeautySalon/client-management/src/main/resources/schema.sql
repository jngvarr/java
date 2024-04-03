-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS visits;

create table if not exists visits
(
    id         SERIAL primary key,
    date       DATE,
    service_id bigint,
    client_id  bigint,
    master_id  bigint
);
