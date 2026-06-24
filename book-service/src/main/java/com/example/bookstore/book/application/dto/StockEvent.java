package com.example.bookstore.book.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEvent {
    private Long orderId;
    private String status; // SUCCESS or FAILED
    private String message;
}
