package com.example.springboot.service;

import com.example.springboot.dto.order.CreateOrderRequestDto;
import com.example.springboot.dto.order.OrderResponseDto;
import com.example.springboot.dto.order.UpdateOrderRequestDto;
import com.example.springboot.dto.orderitem.OrderItemDto;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto saveOrder(Long userId, CreateOrderRequestDto orderRequestDto);

    List<OrderResponseDto> getAll(Pageable pageable);

    Set<OrderItemDto> getOrderItemsByOrderId(Long orderId);

    OrderItemDto getOrderItemFromOrder(Long orderId, Long orderItemId);

    OrderResponseDto updateStatus(Long orderId, UpdateOrderRequestDto orderRequestDto);
}
