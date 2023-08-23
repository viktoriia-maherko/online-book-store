package com.example.springbootintro.repository;

import com.example.springbootintro.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
