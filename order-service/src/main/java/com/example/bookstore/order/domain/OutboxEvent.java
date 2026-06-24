package com.example.bookstore.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    
    @Id
    private String id; // UUID

    @Column(nullable = false)
    private String aggregateId; // e.g., orderId

    @Column(nullable = false)
    private String eventType; // e.g., OrderCreatedEvent

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // JSON representation of the event

    @Column(nullable = false)
    private boolean isPublished;

    private LocalDateTime createdAt;
}
