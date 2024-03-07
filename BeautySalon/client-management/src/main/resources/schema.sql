create table if not exists clients
(
    id           SERIAL primary key,
    firstname    VARCHAR(255) NOT NULL,
    lastname     VARCHAR(255) NOT NULL,
    contact      VARCHAR(255) NOT NULL,
    creation_date DATE
);