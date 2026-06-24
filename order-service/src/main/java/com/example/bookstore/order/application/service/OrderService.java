package com.example.bookstore.order.application.service;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.order.application.dto.OrderRequest;
import com.example.bookstore.order.application.dto.OrderResponse;
import com.example.bookstore.order.domain.Order;
import com.example.bookstore.order.domain.OrderRepository;
import com.example.bookstore.order.infrastructure.feign.BookClient;
import com.example.bookstore.order.infrastructure.feign.BookResponse;
import com.example.bookstore.order.infrastructure.kafka.OrderEventProducer;
import com.example.bookstore.order.application.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final com.example.bookstore.order.domain.OutboxEventRepository outboxEventRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String userId, String userRoles) {
        // 1. Gọi Book Service qua OpenFeign để kiểm tra sách
        ApiResponse<BookResponse> bookApiResp;
        try {
            bookApiResp = bookClient.getBookById(request.getBookId(), userId, userRoles);
        } catch (Exception e) {
            throw new RuntimeException("Không thể kết nối tới Book Service hoặc sách không tồn tại");
        }

        BookResponse book = bookApiResp.getResult();
        if (book == null) {
            throw new RuntimeException("Sách không tồn tại");
        }

        if (book.getStock() < request.getQuantity()) {
            throw new RuntimeException("Không đủ số lượng sách trong kho");
        }

        // 2. Tính tiền và tạo hóa đơn (Trạng thái PENDING)
        Double totalPrice = book.getPrice() * request.getQuantity();

        Order order = Order.builder()
                .userId(userId)
                .bookId(request.getBookId())
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .status("PENDING")
                .build();

        Order savedOrder = orderRepository.save(order);

        String eventId = java.util.UUID.randomUUID().toString();
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(eventId)
                .orderId(savedOrder.getId())
                .bookId(savedOrder.getBookId())
                .quantity(savedOrder.getQuantity())
                .traceId(org.slf4j.MDC.get("traceId"))
                .build();
        
        try {
            com.example.bookstore.order.domain.OutboxEvent outboxEvent = com.example.bookstore.order.domain.OutboxEvent.builder()
                    .id(eventId)
                    .aggregateId(savedOrder.getId().toString())
                    .eventType("OrderCreatedEvent")
                    .payload(objectMapper.writeValueAsString(event))
                    .isPublished(false)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu OutboxEvent", e);
        }

        return OrderResponse.builder()
                .id(savedOrder.getId())
                .userId(savedOrder.getUserId())
                .bookId(savedOrder.getBookId())
                .quantity(savedOrder.getQuantity())
                .totalPrice(savedOrder.getTotalPrice())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }

    /**
     * Cập nhật trạng thái đơn hàng (sử dụng cho luồng xử lý bất đồng bộ từ Kafka).
     *
     * @param orderId ID của đơn hàng
     * @param status  Trạng thái mới (COMPLETED hoặc FAILED)
     */
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
}
