insert into services (title, price, duration, description, consumables)
values ('Мужская стрижка', 299, '20', 'любая стрижка','{}'),
       ('Женская стрижка', 299, '60', 'любая стрижка, кроме "каре"', '{}'),
       ('Простое окрашивание', 1000, '120', 'короткие волосы', '{"краска", "оксидант"}');

select * from services;

SELECT t.* FROM public.services t;

