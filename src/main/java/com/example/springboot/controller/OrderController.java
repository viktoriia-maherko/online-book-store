package com.example.springboot.controller;

import com.example.springboot.dto.order.CreateOrderRequestDto;
import com.example.springboot.dto.order.OrderResponseDto;
import com.example.springboot.dto.order.UpdateOrderRequestDto;
import com.example.springboot.dto.orderitem.OrderItemDto;
import com.example.springboot.model.User;
import com.example.springboot.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    @Operation(summary = "Place an order",
            description = "Place an order")
    public OrderResponseDto saveOrder(
            @RequestBody @Valid CreateOrderRequestDto orderRequestDto,
                                      Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return orderService.saveOrder(user.getId(), orderRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    @Operation(summary = "Get all orders",
            description = "Retrieve user's order history")
    public List<OrderResponseDto> getAllOrders(Pageable pageable) {
        return orderService.getAll(pageable);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{orderId}/items")
    @Operation(summary = "Get order items",
            description = "Retrieve all order items for a specific order")
    public Set<OrderItemDto> getOrderItemsByOrderId(@PathVariable Long orderId) {
        return orderService.getOrderItemsByOrderId(orderId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/api/orders/{orderId}/items/{itemId}")
    @Operation(summary = "Get order item",
            description = "Retrieve a specific OrderItem within an order")
    public OrderItemDto getOrderItemFromOrder(@PathVariable Long orderId,
                                              @PathVariable Long itemId) {
        return orderService.getOrderItemFromOrder(orderId, itemId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}")
    @Operation(summary = "Update order status",
            description = "Update order status")
    public OrderResponseDto updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderRequestDto orderRequestDto
    ) {
        return orderService.updateStatus(id, orderRequestDto);
    }
}
