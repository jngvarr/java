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

-- drop table sevices;