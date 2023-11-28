insert into books (id, title, author, isbn, price, description, is_deleted)
values (1, 'Harry Potter', 'Rouling', '12345678', 100, 'Story about magic world', false);

insert into books_categories (book_id, category_id) values (1, 1);