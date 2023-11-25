package com.example.springboot.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springboot.dto.book.BookDto;
import com.example.springboot.dto.book.CreateBookRequestDto;
import com.example.springboot.model.Book;
import com.example.springboot.model.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-two-books-to-books-table.sql")
            );
        }
    }

    @AfterEach
    void afterEach(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/remove-two-books-from-books-table.sql")
            );
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books")
    void getAll_GivenBooksInCatalog_ReturnsAllBooks() throws Exception {
        BookDto bookDto2 = createBookDto();
        bookDto2.setTitle("The Lord of The Rings");
        bookDto2.setAuthor("Tolkien");
        bookDto2.setIsbn("12345687");
        bookDto2.setPrice(BigDecimal.valueOf(150));
        bookDto2.setDescription("Story about rings of the power");
        BookDto bookDto1 = createBookDto();

        List<BookDto> expected = new ArrayList<>();
        expected.add(bookDto1);
        expected.add(bookDto2);
        String params = "?page=0&size=2";

        MvcResult result = mockMvc.perform(
                get("/books" + params)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.get(0).getTitle(), actual[0].getTitle());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName(("Get book by book id"))
    void getBookById_ValidBookId_ReturnsBookDto() throws Exception {
        BookDto expected = createBookDto();
        Long bookId = expected.getId();
        MvcResult result = mockMvc.perform(
                        get("/books/{id}", bookId)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getTitle(), actual.getTitle());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto expected = createBookDto();

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(
                post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");

    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book by id")
    void updateById_ValidBookId_ReturnsUpdatedBookDto() throws Exception {
        CreateBookRequestDto bookRequestDto = createBookRequestDto();
        BookDto expected = createBookDto();
        Long bookId = expected.getId();
        expected.setTitle("Harry Potter 2");

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(
                        put("/books/{id}", bookId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book by id")
    void deleteById_ValidBookId_Success() throws Exception {
        Long bookId = 1L;
        MvcResult result = mockMvc.perform(
                        delete("/books/{id}",
                                bookId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Search a book by id")
    void search_ValidSearchParams_ReturnsListBookDto() throws Exception {
        String params = "?authors=Rouling&page=0";

        MvcResult result = mockMvc.perform(
                        get("/books/search" + params)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto[] booksDto = objectMapper
                .readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);
        assertNotNull(booksDto);
        assertEquals(1, booksDto.length);

    }

    private Book createBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Harry Potter");
        book.setAuthor("Rouling");
        book.setIsbn("12345678");
        book.setDescription("Story about magic world");
        book.setPrice(BigDecimal.valueOf(100));
        book.setCategories(Set.of(createCategory()));
        return book;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        return category;
    }

    private CreateBookRequestDto createBookRequestDto() {
        CreateBookRequestDto bookRequestDto = new CreateBookRequestDto();
        bookRequestDto.setTitle("Harry Potter");
        bookRequestDto.setAuthor("Rouling");
        bookRequestDto.setIsbn("12345678");
        bookRequestDto.setPrice(BigDecimal.valueOf(100));
        return bookRequestDto;
    }

    private BookDto createBookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Harry Potter");
        bookDto.setAuthor("Rouling");
        bookDto.setIsbn("12345678");
        bookDto.setDescription("Story about magic world");
        bookDto.setPrice(BigDecimal.valueOf(100));
        bookDto.setCategories(Set.of(createCategory()));
        return bookDto;
    }
}
