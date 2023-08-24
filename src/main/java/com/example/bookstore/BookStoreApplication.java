package com.example.bookstore;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class BookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book harryPotter = new Book();
            harryPotter.setTitle("Harry Potter");
            harryPotter.setAuthor("Rouling");
            harryPotter.setIsbn("978-3-16-148410-0");
            harryPotter.setPrice(BigDecimal.valueOf(150));

            bookService.save(harryPotter);

            System.out.println(bookService.findAll());
        };

    }
}
