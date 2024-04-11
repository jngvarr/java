insert into consumables (title, measure, price)
values ('Краска', 'PIECE', 255.7),
       ('Полотенце', 'PIECE', 10),
       ('Простыня', 'PIECE', 20),
       ('Крем для солярия', 'PIECE', 120),
       ('Оксидант1', 'BOTTLE', 179.9),
       ('Оксидант2', 'BOTTLE', 189.9),
       ('Оксидант3', 'BOTTLE', 199.9);

select *
from consumables;

SELECT t.*
FROM public.consumables t;

