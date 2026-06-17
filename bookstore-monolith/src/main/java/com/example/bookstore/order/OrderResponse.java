package com.example.bookstore.order;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderResponse(
    Long id,
    double totalAmount,
    String status,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {}
