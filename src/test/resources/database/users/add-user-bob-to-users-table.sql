insert into users (id, email, password, first_name, last_name, shipping_address)
values (1, 'bob@gmail.com', 'qwerty12345', 'Bob', 'Smith', 'USA');

insert into users_roles (user_id, role_id)
values (1, 2);