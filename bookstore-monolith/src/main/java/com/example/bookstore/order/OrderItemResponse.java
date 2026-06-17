package com.example.bookstore.order;

import lombok.Builder;

@Builder
public record OrderItemResponse(
    Long id,
    Long bookId,
    String bookTitle,
    int quantity,
    double priceAtPurchase
) {}
