package com.example.bookstore.order.infrastructure.kafka;

import com.example.bookstore.order.application.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Đang gửi Event đặt hàng lên Kafka: {}", event);
        kafkaTemplate.send(TOPIC, String.valueOf(event.getOrderId()), event);
    }
}
