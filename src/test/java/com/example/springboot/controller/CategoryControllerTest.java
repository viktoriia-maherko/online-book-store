package com.example.springboot.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springboot.dto.book.BookDtoWithoutCategoryIds;
import com.example.springboot.dto.category.CategoryDto;
import com.example.springboot.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
class CategoryControllerTest {
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
                    new ClassPathResource(
                            "database/categories/add-fiction-category-to-categories-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/add-two-books-to-books-table.sql"
                    )
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
                    new ClassPathResource(
                            "database/categories/remove-fiction-category-from-categories-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books/remove-two-books-from-books-table.sql"
                    )
            );
        }
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Create a new category")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateCategoryRequestDto categoryRequestDto = createCategoryRequestDto();
        CategoryDto expected = createCategoryDto();

        String jsonRequest = objectMapper.writeValueAsString(createCategoryDto());

        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all categories")
    void getAll_GivenCategoriesInCatalog_ReturnsAllCategories() throws Exception {
        String params = "?page=0&size=2";
        CategoryDto categoryDto = createCategoryDto();

        List<CategoryDto> expected = List.of(categoryDto);

        MvcResult result = mockMvc.perform(
                        get("/categories" + params)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), CategoryDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected.get(0).getName(), actual[0].getName());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books from such category")
    void getAll_GivenCategoriesId_ReturnsAllBooksFromSuchCategory() throws Exception {
        Long categoryId = 1L;

        MvcResult result = mockMvc.perform(
                        get("/categories/{id}/books", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(result.getResponse()
                .getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(actual.length, 2);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName(("Get category by category id"))
    void getCategoryById_ValidCategoryId_ReturnsCategoryDto() throws Exception {
        CategoryDto expected = createCategoryDto();
        Long categoryId = expected.getId();
        MvcResult result = mockMvc.perform(
                        get("/categories/{id}", categoryId)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected.getName(), actual.getName());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Update a category by id")
    @Test
    void updateById_ValidCategoryId_ReturnsUpdatedCategoryDto() throws Exception {
        CreateCategoryRequestDto bookRequestDto = createCategoryRequestDto();
        bookRequestDto.setName("Non-fiction");
        CategoryDto expected = createCategoryDto();
        Long categoryId = expected.getId();
        expected.setName("Non-fiction");

        String jsonRequest = objectMapper.writeValueAsString(bookRequestDto);

        MvcResult result = mockMvc.perform(
                        put("/categories/{id}", categoryId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), CategoryDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a category by id")
    void deleteById_ValidCategoryId_Success() throws Exception {
        Long categoryId = 1L;
        MvcResult result = mockMvc.perform(
                        delete("/categories/{id}",
                                categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private CategoryDto createCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Fiction");
        return categoryDto;
    }

    private CreateCategoryRequestDto createCategoryRequestDto() {
        CreateCategoryRequestDto categoryRequestDto = new CreateCategoryRequestDto();
        categoryRequestDto.setName("Fiction");
        return categoryRequestDto;
    }
}
