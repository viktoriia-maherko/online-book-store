package com.example.springboot.dto.order;

import com.example.springboot.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderRequestDto {
    @NotNull
    private Order.Status orderStatus;
}
