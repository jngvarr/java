-- create table clients;
-- use clients;
CREATE SCHEMA IF NOT EXISTS employees;

create table if not exists employees
(
    id         SERIAL primary key,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    contact    VARCHAR(255) NOT NULL,
    dob        DATE         NOT NULL,
    function   varchar(50)  NOT NULL
);
