-- create table clients;
-- use clients;
-- CREATE SCHEMA IF NOT EXISTS services;

create table if not exists clients
(
    id         SERIAL primary key,
    first_name varchar(25) NOT NULL,
    last_name  VARCHAR(50),
    contact    VARCHAR(50) NOT NULL,
    dob        DATE
);

--  drop table clients;