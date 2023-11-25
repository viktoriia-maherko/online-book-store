package com.example.springboot.service;

import com.example.springboot.dto.book.BookDto;
import com.example.springboot.dto.book.BookDtoWithoutCategoryIds;
import com.example.springboot.dto.book.BookSearchParameters;
import com.example.springboot.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId, Pageable pageable);

    BookDto updateById(CreateBookRequestDto requestDto, Long id);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);
}
