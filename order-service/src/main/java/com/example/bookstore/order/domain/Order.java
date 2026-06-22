package com.example.bookstore.order.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // Lấy từ Header X-User-Id
    
    private Long bookId; // Tham chiếu sang book-service

    private int quantity;
    
    private Double totalPrice;

    private String status; // PENDING, COMPLETED, FAILED

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
