package com.example.springboot.repository.orderitem;

import com.example.springboot.model.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>,
        JpaSpecificationExecutor<OrderItem> {
    Optional<OrderItem> findOrderItemByIdAndOrderId(Long orderItemId, Long orderId);
}
