drop database if exists test;
create database test;
use test;
create table Road (
Name varchar(100),
Fast double,
Smooth double,
primary key (Name)
);
insert into Road values ('Old Mill Road', '67.45', '64.61');
insert into Road values ('Paerdegat 7th Street', '34.97', '7.36');
insert into Road values ('Mc Guinness Boulevard', '51.65', '92.2');
insert into Road values ('Elmwood Avenue', '31.44', '96.9');
insert into Road values ('Skidmore Place', '63.11', '43.64');
insert into Road values ('Aster Court', '25.73', '94.13');
insert into Road values ('15th Avenue', '46.47', '78.98');
insert into Road values ('Avenue O', '91.28', '63.66');
insert into Road values ('Dearborn Court', '26.01', '9.28');
insert into Road values ('Brighton 4th Lane', '46.69', '2.52');
