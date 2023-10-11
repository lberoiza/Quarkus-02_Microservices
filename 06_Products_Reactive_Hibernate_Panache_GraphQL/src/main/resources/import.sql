-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

insert into products (id, name, description) values (1, 'Television Sony', 'Television Sony 48 inch plasma led');
insert into products (id, name, description) values (2, 'Bicycle Cube', 'Electro-Bicycle Cube 26 inch Wheels');
insert into products (id, name, description) values (3, 'Ferrari Diablo red', 'Auto Ferrari Diablo Red');
insert into products (id, name, description) values (4, 'Airplane', 'Jet Airplane');
insert into products (id, name, description) values (5, 'Mansion', 'Big Mansion 2000 mt2');
insert into products (id, name, description) values (6, 'Hummer Limo', 'Big Luxus Auto ');
insert into products (id, name, description) values (7, 'Department', 'Department in London');

SELECT setval('products_seq', (SELECT MAX(id) FROM products));