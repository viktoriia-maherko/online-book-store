package com.example.springboot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.springboot.model.Role;
import com.example.springboot.model.ShoppingCart;
import com.example.springboot.model.User;
import com.example.springboot.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("""
            Find shopping cart by valid user id
            """)
    @Sql(scripts = {
            "classpath:database/users/add-user-bob-to-users-table.sql",
            "classpath:database/books/add-one-book-to-books-table.sql",
            "classpath:database/categories/add-fiction-category-to-categories-table.sql",
            "classpath:database/shoppingcarts/add-shopping-cart-to-shopping-carts-table.sql",
            "classpath:database/cartitems/add-cart-item-to-cart-items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/users/remove-user-bob-from-users-table.sql",
            "classpath:database/books/remove-one-book-from-books-table.sql",
            "classpath:database/shoppingcarts/remove-shopping-cart-from-shopping-carts-table.sql",
            "classpath:database/cartitems/remove-cart-item-from-cart-items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findShoppingCartByUserId_ValidUserId_ReturnsOneShoppingCart() {
        User user = createUser();
        Long userId = user.getId();
        Optional<ShoppingCart> actual = shoppingCartRepository.findShoppingCartByUserId(userId);
        assertTrue(actual.isPresent());
        assertEquals(1L, actual.get().getId());
        assertEquals(user.getEmail(), actual.get().getUser().getEmail());
    }

    @Test
    @DisplayName("""
            Find shopping cart by invalid user id
            """)
    @Sql(scripts = {
            "classpath:database/users/add-user-bob-to-users-table.sql",
            "classpath:database/books/add-one-book-to-books-table.sql",
            "classpath:database/categories/add-fiction-category-to-categories-table.sql",
            "classpath:database/shoppingcarts/add-shopping-cart-to-shopping-carts-table.sql",
            "classpath:database/cartitems/add-cart-item-to-cart-items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = {
            "classpath:database/users/remove-user-bob-from-users-table.sql",
            "classpath:database/books/remove-one-book-from-books-table.sql",
            "classpath:database/shoppingcarts/remove-shopping-cart-from-shopping-carts-table.sql",
            "classpath:database/cartitems/remove-cart-item-from-cart-items-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findShoppingCartByUserId_InvalidUserId_ReturnsEmptyOptional() {
        Long userId = -1L;
        Optional<ShoppingCart> actual = shoppingCartRepository.findShoppingCartByUserId(userId);
        assertTrue(actual.isEmpty());
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
        role.setId(1L);
        role.setRoleName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(role));
        user.setDeleted(false);
        return user;
    }
}
