package com.example.bookstore.book.infrastructure.kafka;

import com.example.bookstore.book.application.dto.OrderCreatedEvent;
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

    @KafkaListener(topics = "order-events", groupId = "book-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Nhận được Event đặt hàng từ Kafka: {}", event);
        try {
            // Thực hiện trừ kho
            manageBookUseCase.updateStock(event.getBookId(), event.getQuantity());
            log.info("Trừ kho thành công cho sách ID: {}, số lượng: {}", event.getBookId(), event.getQuantity());
        } catch (Exception e) {
            log.error("Lỗi khi trừ kho cho sách ID: {}. Cần kích hoạt cơ chế bù trừ (Compensation/Saga) nếu cần thiết. Chi tiết: {}", event.getBookId(), e.getMessage());
            // Trong hệ thống thực tế, nếu trừ kho thất bại, ta phải bắn lại Event "DeductFailed" để OrderService đổi trạng thái đơn hàng thành FAILED.
        }
    }
}
