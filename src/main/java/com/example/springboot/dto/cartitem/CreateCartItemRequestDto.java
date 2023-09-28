package com.example.springboot.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateCartItemRequestDto {
    @Min(1)
    private Long bookId;
    @Min(1)
    private int quantity;
}
