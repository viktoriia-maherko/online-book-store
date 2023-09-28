package com.example.springboot.repository.shoppingcart;

import com.example.springboot.model.Category;
import com.example.springboot.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<Category, Long>,
        JpaSpecificationExecutor<Category> {
    @Query("""
            FROM ShoppingCart sc
            LEFT JOIN FETCH sc.cartItems ci
            LEFT JOIN FETCH ci.book
            WHERE sc.user.id = :id
            """)
    Optional<ShoppingCart> findShoppingCartByUserId(Long id);
}
