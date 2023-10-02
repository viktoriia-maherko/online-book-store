package com.example.springboot.service.impl;

import com.example.springboot.dto.order.CreateOrderRequestDto;
import com.example.springboot.dto.order.OrderResponseDto;
import com.example.springboot.dto.order.UpdateOrderRequestDto;
import com.example.springboot.dto.orderitem.OrderItemDto;
import com.example.springboot.mapper.OrderItemMapper;
import com.example.springboot.mapper.OrderMapper;
import com.example.springboot.model.Order;
import com.example.springboot.model.OrderItem;
import com.example.springboot.model.User;
import com.example.springboot.repository.order.OrderRepository;
import com.example.springboot.repository.orderitem.OrderItemRepository;
import com.example.springboot.repository.user.UserRepository;
import com.example.springboot.service.OrderService;
import com.example.springboot.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private UserRepository userRepository;
    private ShoppingCartService shoppingCartService;
    private OrderItemMapper orderItemMapper;
    private OrderMapper orderMapper;

    @Transactional
    @Override
    public OrderResponseDto saveOrder(Long userId, CreateOrderRequestDto orderRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(()
                        -> new RuntimeException("Can't find user by id " + userId));
        Order order = new Order();
        order.setUser(user);
        Set<OrderItem> orders = shoppingCartService.getShoppingCartByUserId(userId)
                .getCartItems()
                .stream()
                .map(orderItemMapper::toEntity)
                .collect(Collectors.toSet());
        order.setOrderItems(getOrderItemsByUserId(userId));
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public Set<OrderItemDto> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new RuntimeException("Can't find order by id " + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public OrderItemDto getOrderItemFromOrder(Long orderId, Long orderItemId) {
        OrderItem orderItem = orderItemRepository
                .findOrderItemByIdAndOrderId(orderItemId, orderId)
                .orElseThrow(()
                        -> new RuntimeException("Can't find order item by order id "
                        + orderId + " and order item id " + orderItemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Transactional
    @Override
    public OrderResponseDto updateStatus(Long orderId, UpdateOrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(()
                -> new RuntimeException("Can't find order by id " + orderId));
        order.setStatus(orderRequestDto.getOrderStatus());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    private Set<OrderItem> getOrderItemsByUserId(Long userId) {
        return shoppingCartService.getShoppingCartByUserId(userId).getCartItems()
                .stream()
                .map(orderItemMapper::toEntity)
                .collect(Collectors.toSet());
    }
}
