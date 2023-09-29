package com.example.springboot.repository.orderitem;

import com.example.springboot.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>,
        JpaSpecificationExecutor<OrderItem> {
    OrderItem findOrderItemByIdAndOrderId(Long orderItemId, Long orderId);
}
