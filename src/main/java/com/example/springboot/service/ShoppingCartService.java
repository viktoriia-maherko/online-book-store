package com.example.springboot.service;

import com.example.springboot.dto.cartitem.CartItemDto;
import com.example.springboot.dto.cartitem.CreateCartItemRequestDto;
import com.example.springboot.dto.cartitem.UpdateCartItemRequestDto;
import com.example.springboot.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCartByUserId(Long userId);

    CartItemDto addBookToTheShoppingCart(Long userId, CreateCartItemRequestDto cartItemRequestDto);

    CartItemDto updateBookInTheShoppingCart(Long userId,
                                            UpdateCartItemRequestDto cartItemRequestDto);

    void removeBookFromTheShoppingCart(Long cartItemId);

}
