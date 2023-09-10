package com.example.springboot.service;

import com.example.springboot.dto.BookDto;
import com.example.springboot.dto.BookSearchParameters;
import com.example.springboot.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    BookDto updateById(CreateBookRequestDto requestDto, Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);
}
