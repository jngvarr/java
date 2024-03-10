-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS clients;

create table if not exists clients
(
    id           SERIAL primary key,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    contact      VARCHAR(255) NOT NULL,
    dob DATE
);
