package com.example.bookstore.order.application.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String userId;
    private Long bookId;
    private int quantity;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
}
