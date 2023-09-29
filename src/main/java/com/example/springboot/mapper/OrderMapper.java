package com.example.springboot.mapper;

import com.example.springboot.config.MapperConfig;
import com.example.springboot.dto.order.CreateOrderRequestDto;
import com.example.springboot.dto.order.OrderResponseDto;
import com.example.springboot.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderResponseDto toDto(Order order);

    Order toEntity(CreateOrderRequestDto orderRequestDto);
}
