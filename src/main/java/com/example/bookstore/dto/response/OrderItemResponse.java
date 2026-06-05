package com.example.bookstore.dto.response;

import lombok.*;

/**
 * Data Transfer Object representing an item details within an order.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private int quantity;
    private double priceAtPurchase;
}
