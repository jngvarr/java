-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS consumables;

create table if not exists consumables
(
    id      SERIAL primary key,
    title   VARCHAR(50) NOT NULL,
    measure VARCHAR(50) NOT NULL DEFAULT 'PIECE',
    price   DECIMAL     NOT NULL
);
