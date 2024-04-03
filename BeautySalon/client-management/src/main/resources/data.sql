insert into visits (date, service_id, client_id, master_id)
values (now(), 1, 1, 1),
       (now(), 2, 2, 2);

select * from visits;

SELECT t.* FROM public.visits t;

drop table visits;
drop schema visits;

