package com.example.bookstore.book.infrastructure.kafka;

import com.example.bookstore.book.application.dto.OrderCreatedEvent;
import com.example.bookstore.book.application.dto.StockEvent;
import com.example.bookstore.book.domain.port.in.ManageBookUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ManageBookUseCase manageBookUseCase;
    private final StockEventProducer stockEventProducer;

    @KafkaListener(topics = "order-events", groupId = "book-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            // Trừ kho
            manageBookUseCase.updateStock(event.getBookId(), event.getQuantity());

            // Bắn Event thành công về Order Service
            StockEvent stockEvent = StockEvent.builder()
                    .orderId(event.getOrderId())
                    .status("SUCCESS")
                    .message("Trừ kho thành công")
                    .build();
            stockEventProducer.sendStockEvent(stockEvent);

        } catch (Exception e) {
            // Bắn Event thất bại về Order Service
            StockEvent stockEvent = StockEvent.builder()
                    .orderId(event.getOrderId())
                    .status("FAILED")
                    .message(e.getMessage())
                    .build();
            stockEventProducer.sendStockEvent(stockEvent);
        }
    }
}
