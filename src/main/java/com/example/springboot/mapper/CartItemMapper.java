package com.example.springboot.mapper;

import com.example.springboot.config.MapperConfig;
import com.example.springboot.dto.cartitem.CartItemDto;
import com.example.springboot.dto.cartitem.CreateCartItemRequestDto;
import com.example.springboot.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);

    @Mapping(source = "bookId", target = "book.id")
    CartItem toEntity(CreateCartItemRequestDto cartItemRequestDto);
}
