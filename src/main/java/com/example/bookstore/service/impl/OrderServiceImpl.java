package com.example.bookstore.service.impl;

import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.service.EmailService;
import com.example.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public void placeOrder(Long bookId, int quantity) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
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

        // 8. Gửi Email thông báo (Chạy ngầm, không bắt user phải chờ)
        emailService.sendOrderConfirmation(user.getUsername());
    }
}
