package com.example.bookstore.book.infrastructure.kafka;

import com.example.bookstore.book.application.dto.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "stock-events";

    public void sendStockEvent(StockEvent event) {
        log.info("Đang gửi Event trừ kho lên Kafka: {}", event);
        kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);
    }
}
