-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS services;

create table if not exists services
(
    id           SERIAL primary key,
    title    VARCHAR(255) NOT NULL,
    duration INT NOT NULL
);
