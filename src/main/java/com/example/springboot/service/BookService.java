package com.example.springboot.service;

import com.example.springboot.dto.BookDto;
import com.example.springboot.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
