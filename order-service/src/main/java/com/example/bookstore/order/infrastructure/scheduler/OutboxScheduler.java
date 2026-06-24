package com.example.bookstore.order.infrastructure.scheduler;

import com.example.bookstore.order.application.dto.OrderCreatedEvent;
import com.example.bookstore.order.domain.OutboxEvent;
import com.example.bookstore.order.domain.OutboxEventRepository;
import com.example.bookstore.order.infrastructure.kafka.OrderEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByIsPublishedFalse();
        if (!events.isEmpty()) {
            log.info("Found {} outbox events to publish", events.size());
        }

        for (OutboxEvent event : events) {
            try {
                if ("OrderCreatedEvent".equals(event.getEventType())) {
                    OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(event.getPayload(), OrderCreatedEvent.class);
                    orderEventProducer.sendOrderCreatedEvent(orderCreatedEvent);
                    
                    event.setPublished(true);
                    outboxEventRepository.save(event);
                    log.info("Published and updated outbox event: {}", event.getId());
                }
            } catch (Exception e) {
                log.error("Failed to process outbox event: {}", event.getId(), e);
            }
        }
    }
}
