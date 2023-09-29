package com.example.springboot.mapper;

import com.example.springboot.config.MapperConfig;
import com.example.springboot.dto.cartitem.CartItemDto;
import com.example.springboot.dto.orderitem.OrderItemDto;
import com.example.springboot.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(source = "bookId", target = "book.id")
    OrderItem toEntity(CartItemDto cartItemDto);
}
