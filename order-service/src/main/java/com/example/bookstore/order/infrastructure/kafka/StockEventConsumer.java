package com.example.bookstore.order.infrastructure.kafka;

import com.example.bookstore.order.application.dto.StockEvent;
import com.example.bookstore.order.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockEventConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "stock-events", groupId = "order-group")
    public void consumeStockEvent(StockEvent event) {
        if ("SUCCESS".equals(event.getStatus())) {
            orderService.updateOrderStatus(event.getOrderId(), "COMPLETED");
        } else {
            orderService.updateOrderStatus(event.getOrderId(), "FAILED");
        }
    }
}
