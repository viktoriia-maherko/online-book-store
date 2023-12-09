package com.example.springboot.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springboot.dto.cartitem.CreateCartItemRequestDto;
import com.example.springboot.dto.cartitem.UpdateCartItemRequestDto;
import com.example.springboot.model.Role;
import com.example.springboot.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
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
                            "database/users/add-user-bob-to-users-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books"
                                    + "/add-one-book-to-books-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories"
                                    + "/add-fiction-category-to-categories-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingcarts"
                                    + "/add-shopping-cart-to-shopping-carts-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/cartitems/add-cart-item-to-cart-items-table.sql"
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
                            "database/cartitems/remove-cart-item-from-cart-items-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/users/remove-user-bob-from-users-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/books"
                                    + "/remove-one-book-from-books-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/categories"
                                    + "/remove-fiction-category-from-categories-table.sql"
                    )
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingcarts"
                                    + "/remove-shopping-cart-from-shopping-carts-table.sql"
                    )
            );
        }
    }

    @Test
    @DisplayName("Add book to the shopping cart")
    @WithMockUser(username = "bob@gmail.com", roles = "USER")
    void addBookToTheShoppingCart_ValidRequestDto_Success() throws Exception {
        CreateCartItemRequestDto cartItemRequestDto = createCartItemRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);
        mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity")
                        .value(cartItemRequestDto.getQuantity()))
                .andReturn();
    }

    @Test
    @DisplayName(value = "Get shopping cart")
    @WithMockUser(username = "bob@gmail.com", roles = "USER")
    void getShoppingCart_ValidUserId_Success() throws Exception {
        User user = createUser();
        Long userId = user.getId();
        mockMvc.perform(
                get("/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "bob@gmail.com", roles = "USER")
    @DisplayName("Update quantity of a book in the shopping cart")
    void updateQuantityOfBooks_ValidRequestDtoAndCartItemId_ReturnUpdatedCartItem()
            throws Exception {
        UpdateCartItemRequestDto cartItemRequestDto = new UpdateCartItemRequestDto();
        cartItemRequestDto.setQuantity(200);
        String jsonRequest = objectMapper.writeValueAsString(cartItemRequestDto);

        mockMvc.perform(
                        put("/cart/cart-items/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.quantity").value(200))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "bob@gmail.com", roles = "USER")
    @DisplayName("Delete a book from the shopping cart")
    void deleteBookFromShoppingCartByCartItemId_ValidCartItemId_Success()
            throws Exception {
        Long cartItemId = 1L;
        mockMvc.perform(
                        delete("/cart/cart-items/{cartItemId}",
                                cartItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private CreateCartItemRequestDto createCartItemRequestDto() {
        CreateCartItemRequestDto cartItemRequestDto = new CreateCartItemRequestDto();
        cartItemRequestDto.setQuantity(100);
        cartItemRequestDto.setBookId(1L);
        return cartItemRequestDto;
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("bob@gmail.com");
        user.setPassword("qwerty12345");
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setShippingAddress("USA");
        Role role = new Role();
        role.setRoleName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setDeleted(false);
        return user;
    }
}
