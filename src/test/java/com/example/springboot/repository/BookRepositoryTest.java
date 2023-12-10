package com.example.springboot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.springboot.model.Book;
import com.example.springboot.repository.book.BookRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all books by category id
            """)
    @Sql(scripts = {
            "classpath:database/books/add-one-book-to-books-table.sql",
            "classpath:database/categories/add-fiction-category-to-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            "classpath:database/books/remove-one-book-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByCategoryId_ValidCategoryId_ReturnsOneBook() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllByCategoryId(1L, pageable);
        assertEquals(1, actual.size());
        assertEquals("Harry Potter", actual.get(0).getTitle());
    }

    @Test
    @DisplayName("""
            Find all books by category id
            """)
    @Sql(scripts = {
            "classpath:database/books/add-two-books-to-books-table.sql",
            "classpath:database/categories/add-fiction-category-to-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            "classpath:database/books/remove-two-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findAllByCategoryId_ValidCategoryId_ReturnsTwoBook() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> actual = bookRepository.findAllByCategoryId(1L, pageable);
        assertEquals(2, actual.size());
    }
}
