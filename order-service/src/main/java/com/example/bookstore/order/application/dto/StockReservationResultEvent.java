package com.example.bookstore.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResultEvent {
    private Long orderId;
    private Long bookId;
    private int quantity;
    private boolean success;
    private String reason;
    private String traceId;
}
