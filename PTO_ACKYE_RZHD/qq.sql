set search_path = salon;

create schema IF NOT EXISTS salon;

create table if not exists services
(
    id          bigint generated always as identity primary key,
    title       text    not null,
    price       numeric not null,
    duration    integer not null,
    description text    not null
);
create table if not exists consumables
(
    id    bigint generated always as identity primary key,
    title text NOT NULL,
    unit  text DEFAULT 'PIECE',
    price DECIMAL
);

create table if not exists clients
(
    id         bigint generated always as identity primary key,
    first_name TEXT NOT NULL,
    last_name  TEXT,
    contact    TEXT NOT NULL,
    dob        DATE
);

create table if not exists visits
(
    id         bigint generated always as identity primary key,
    date       DATE   NOT NULL,
    start_time TIME   NOT NULL,
    service_id bigint not null,
    client_id  bigint not null,
    master_id  bigint not null
);

create table if not exists employees
(
    id         bigint generated always as identity primary key,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    contact    TEXT NOT NULL,
    dob        DATE NOT NULL,
    function   TEXT NOT NULL
);

create table service_to_consumables
(
    id            bigint generated always as identity,
    consumable_id bigint not null
        constraint service_to_consumables_fk1
            references consumables (id) on delete cascade,
    service_id    bigint not null
        constraint service_to_consumables_fk2
            references services (id) on delete cascade
);

create table if not exists users
(
    id         bigint generated always as identity primary key,
    first_name TEXT,
    last_name  TEXT,
    contact    TEXT NOT NULL,
    dob        DATE,
    user_name  TEXT NOT NULL,
    email      TEXT NOT NULL,
    password   TEXT NOT NULL,
    enabled   TEXT DEFAULT 'true'
);

create table if not exists tokens
(
    id         bigint generated always as identity primary key,
    token      TEXT      NOT NULL,
    valid_thru TIMESTAMP NOT NULL,
    revoked    text default false
);


create table if not exists authorities
(
    id   bigint generated always as identity primary key,
    name TEXT NOT NULL
);

create table if not exists user_to_tokens
(
    id       bigint generated always as identity,
    token_id bigint not null
        constraint user_to_tokens_fk1
            references tokens (id),
    user_id  bigint not null
        constraint user_to_tokens_fk2
            references users (id)
);

create table if not exists user_to_authorities
(
    id           bigint generated always as identity,
    authority_id bigint not null
        constraint user_to_authorities_fk1
            references authorities (id),
    user_id      bigint not null
        constraint user_to_authorities_fk2
            references users (id)
);

insert into service_to_consumables (service_id, consumable_id)
select s.id, c.id
from services s,
     consumables c
where s.title = 'Простое окрашивание'
  and c.title in ('Краска',
                  'Полотенце'
    );

insert into visits (date, start_time, service_id, client_id, master_id)
values ('2006-12-11', '12:00:01', 1, 2, 3),
       ('2006-12-12', '12:00:02', 1, 1, 1),
       ('2006-12-13', '12:00:03', 1, 1, 2);

insert into clients (first_name, last_name, contact, dob)
values ('Иван', 'Иванов', '+79998887766', '2001-01-01'),
       ('Пётр', 'Петров', '+79995554433', '2002-02-02');

insert into services (title, price, duration, description)
values ('Мужская стрижка', 299, '20', 'любая стрижка'),
       ('Женская стрижка', 299, '60', 'любая стрижка, кроме "каре"'),
       ('Простое окрашивание', 1000, '120', 'короткие волосы');

insert into employees (first_name, last_name, contact, dob, function)
values ('Светлана', 'Кукушкина', '+75598887766', '2001-08-07', 'HAIRDRESSER'),
       ('Евгения', 'Матрёшкина', '+79445554411', '2002-05-19', 'NAILMASTER'),
       ('Инна', 'Поварёжкина', '+79995554433', '2003-07-13', 'ADMIN'),
       ('Екатерина', 'Щеглова', '+79545554412', '2002-05-19', 'CLEANING');

insert into consumables (title, unit, price)
values ('Краска', 'Штука', 255.7),
       ('Полотенце', 'Штука', 10),
       ('Простыня', 'Штука', 20),
       ('Крем для солярия', 'Штука', 120),
       ('Оксидант1', 'Бутылка', 179.9),
       ('Оксидант2', 'Бутылка', 189.9),
       ('Оксидант3', 'Бутылка', 199.9);



