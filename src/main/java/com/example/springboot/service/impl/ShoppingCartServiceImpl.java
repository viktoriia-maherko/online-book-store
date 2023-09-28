package com.example.springboot.service.impl;

import com.example.springboot.dto.cartitem.CartItemDto;
import com.example.springboot.dto.cartitem.CreateCartItemRequestDto;
import com.example.springboot.dto.cartitem.UpdateCartItemRequestDto;
import com.example.springboot.dto.shoppingcart.ShoppingCartDto;
import com.example.springboot.mapper.CartItemMapper;
import com.example.springboot.mapper.ShoppingCartMapper;
import com.example.springboot.model.CartItem;
import com.example.springboot.model.ShoppingCart;
import com.example.springboot.repository.cartitem.CartItemRepository;
import com.example.springboot.repository.shoppingcart.ShoppingCartRepository;
import com.example.springboot.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private ShoppingCartRepository shoppingCartRepository;
    private CartItemRepository cartItemRepository;
    private ShoppingCartMapper shoppingCartMapper;
    private CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getShoppingCartByUserId(Long userId) {
        ShoppingCart shoppingCartByUserId = shoppingCartRepository
                .findShoppingCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Can't find a shopping cart by user id: " + userId
                ));
        return shoppingCartMapper.toDto(shoppingCartByUserId);
    }

    @Override
    @Transactional
    public CartItemDto addBookToTheShoppingCart(
            Long userId,
            CreateCartItemRequestDto cartItemRequestDto
    ) {
        CartItem cartItem = cartItemMapper.toEntity(cartItemRequestDto);
        ShoppingCart shoppingCart = shoppingCartRepository
                .findShoppingCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException(
                "Can't find a shopping cart by user id: " + userId
        ));
        cartItem.setShoppingCart(shoppingCart);
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    @Transactional
    public CartItemDto updateBookInTheShoppingCart(
            Long cartItemId,
            UpdateCartItemRequestDto cartItemRequestDto
    ) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException(
                "Can't find a cart item by id: " + cartItemId
        ));
        cartItem.setQuantity(cartItemRequestDto.getQuantity());
        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void removeBookFromTheShoppingCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException(
                "Can't find cart item by id: " + cartItemId
        ));
        cartItemRepository.delete(cartItem);
    }
}
