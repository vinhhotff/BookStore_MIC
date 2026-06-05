package com.example.bookstore.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing complete order details.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private double totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
