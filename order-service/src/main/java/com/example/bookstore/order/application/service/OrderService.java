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
    private final OrderEventProducer orderEventProducer;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request, String userId, String userRoles) {
        log.info("Bắt đầu xử lý đặt hàng cho user: {}, book: {}", userId, request.getBookId());

        // 1. Gọi Book Service qua OpenFeign để kiểm tra sách
        ApiResponse<BookResponse> bookApiResp;
        try {
            bookApiResp = bookClient.getBookById(request.getBookId(), userId, userRoles);
        } catch (Exception e) {
            log.error("Lỗi khi gọi Book Service: {}", e.getMessage());
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

        // TODO: (Chặng sau) Publish event ra Kafka để BookService trừ kho thực tế.
        // Gửi Event ra Kafka
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .bookId(savedOrder.getBookId())
                .quantity(savedOrder.getQuantity())
                .build();
        orderEventProducer.sendOrderCreatedEvent(event);

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
}
