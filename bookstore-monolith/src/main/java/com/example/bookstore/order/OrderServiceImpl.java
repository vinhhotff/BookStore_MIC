package com.example.bookstore.order;

import com.example.bookstore.order.OrderResponse;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.order.OrderMapper;
import com.example.bookstore.book.Book;
import com.example.bookstore.order.Order;
import com.example.bookstore.order.OrderItem;
import com.example.bookstore.user.User;
import com.example.bookstore.book.BookRepository;
import com.example.bookstore.order.OrderRepository;
import com.example.bookstore.user.UserRepository;
import com.example.bookstore.order.EmailService;
import com.example.bookstore.order.OrderService;
import com.example.bookstore.common.RetryOnConflict;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

/**
 * Service implementation containing business logic for Order operations.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    @RetryOnConflict(maxRetries = 3, delayMs = 100)
    public void placeOrder(Long bookId, int quantity) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        if (book.getStock() < quantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        book.setStock(book.getStock() - quantity);
        bookRepository.save(book);

        Order order = Order.builder()
                .user(user)
                .status("COMPLETED")
                .totalAmount(book.getPrice() * quantity)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .book(book)
                .order(order)
                .quantity(quantity)
                .priceAtPurchase(book.getPrice())
                .build();

        order.setItems(List.of(orderItem));
        orderRepository.save(order);

        // Send Email Confirmation asynchronously ONLY after transaction commits successfully
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    emailService.sendOrderConfirmation(user.getEmail());
                }
            });
        } else {
            emailService.sendOrderConfirmation(user.getEmail());
        }
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Order> orders = orderRepository.findByUserIdWithItems(user.getId());
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAllWithItems();
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }
}
