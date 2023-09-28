package com.example.springboot.controller;

import com.example.springboot.dto.cartitem.CartItemDto;
import com.example.springboot.dto.cartitem.CreateCartItemRequestDto;
import com.example.springboot.dto.cartitem.UpdateCartItemRequestDto;
import com.example.springboot.dto.shoppingcart.ShoppingCartDto;
import com.example.springboot.model.User;
import com.example.springboot.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get a shopping cart",
            description = "Get a shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCartByUserId(user.getId());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Add book to the shopping cart",
            description = "Add book to the shopping cart")
    public CartItemDto addBookToTheShoppingCart(
            @RequestBody @Valid CreateCartItemRequestDto cartItemRequestDto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToTheShoppingCart(user.getId(), cartItemRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "/cart-items/{cartItemId}")
    @Operation(summary = "Update a quantity",
            description = "Update quantity of a book in the shopping cart")
    public CartItemDto updateQuantity(
            @RequestBody @Valid UpdateCartItemRequestDto cartItemRequestDto,
            @PathVariable Long cartItemId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService
                .updateBookInTheShoppingCart(cartItemId, cartItemRequestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole(ROLE_USER)")
    @DeleteMapping(value = "/cart-items/{cartItemId}")
    @Operation(summary = "Delete a book",
            description = "Remove a book from the shopping cart")
    public void deleteBookFromTheShoppingCart(@PathVariable Long cartItemId) {
        shoppingCartService.removeBookFromTheShoppingCart(cartItemId);
    }
}
