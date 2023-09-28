package com.example.springboot.dto.cartitem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    @Min(1)
    private Long bookId;
    @NotNull
    private String bookTitle;
    @Min(1)
    private int quantity;
}
