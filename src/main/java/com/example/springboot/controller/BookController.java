package com.example.springboot.controller;

import com.example.springboot.dto.BookDto;
import com.example.springboot.dto.BookSearchParameters;
import com.example.springboot.dto.CreateBookRequestDto;
import com.example.springboot.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping(value = "/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    public BookDto save(@RequestBody CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @PutMapping(value = "/{id}")
    public BookDto updateById(@RequestBody CreateBookRequestDto requestDto,
                              @PathVariable Long id) {
        return bookService.updateById(requestDto, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @GetMapping(value = "/search")
    public List<BookDto> search(BookSearchParameters searchParameters) {
        return bookService.search(searchParameters);
    }
}
