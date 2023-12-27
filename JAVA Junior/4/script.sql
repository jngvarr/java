use test;
CREATE TABLE `test`. `table`(id INT NOT NULL PRIMARY KEY auto_increment, firstname VARCHAR(45),lastname VARCHAR(45));

insert `test`.`table` 
(
`firstname`, `lastname`
)
VALUES
(
'Иванов', 'Иван'
);

drop table `magic`;

select * from `table`;

CREATE TABLE `test`. `magic`
(
`id` INT NOT NULL PRIMARY KEY auto_increment,
`название` VARCHAR(45) null,
`повреждение` int null,
`атака` int null,
`броня` int null
);
select * from `magic`;
drop table `magic`;
delete from `magic`;
