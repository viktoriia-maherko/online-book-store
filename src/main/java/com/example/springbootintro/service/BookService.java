package com.example.springbootintro.service;

import com.example.springbootintro.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
