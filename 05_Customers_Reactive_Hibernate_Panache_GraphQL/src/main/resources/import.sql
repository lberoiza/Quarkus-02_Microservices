-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;


insert into customers (id, code, accountNumber, name, surname, phone, address) values (1, 'LAB', '987654321', 'Luis Alberto', 'Beroiza Osses', '123546', 'Spring Even Street');

insert into products (id, customer, producId) values (1, 1, 1);
insert into products (id, customer, productId) values (2, 1, 2);
insert into products (id, customer, productId) values (3, 1, 3);
insert into products (id, customer, productId) values (4, 1, 4);


insert into customers (id, code, accountNumber, name, surname, phone, address) values (2, 'SMA', '654741369', 'Silvia', 'Martinez', '987456', 'Test Street 01');

insert into products (id, customer, productId) values (5, 2, 2);
insert into products (id, customer, productId) values (6, 2, 3);

SELECT setval('customers_seq', (SELECT MAX(id) FROM customers));
SELECT setval('products_seq', (SELECT MAX(id) FROM products));