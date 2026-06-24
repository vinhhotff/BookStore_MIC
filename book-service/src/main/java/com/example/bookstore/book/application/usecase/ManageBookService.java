package com.example.bookstore.book.application.usecase;

import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.model.Category;
import com.example.bookstore.book.domain.port.in.ManageBookUseCase;
import com.example.bookstore.book.domain.port.out.BookRepositoryPort;
import com.example.bookstore.book.domain.port.out.CategoryRepositoryPort;
import com.example.bookstore.book.mapper.BookMapper;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManageBookService implements ManageBookUseCase {

    private final BookRepositoryPort bookRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final BookMapper bookMapper;
    private final com.example.bookstore.book.domain.port.out.ReviewPort reviewPort;
    private final com.example.bookstore.book.domain.ProcessedEventRepository processedEventRepository;

    @Override
    @Transactional
    public Book createBook(Book book) {
        Category category = categoryRepositoryPort.findById(book.category().id())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Book newBook = bookMapper.updateCategory(book, category);
        return bookRepositoryPort.save(newBook);
    }

    @Override
    public Optional<Book> getBook(Long id) {
        return bookRepositoryPort.findById(id).map(book -> {
            Double rating = reviewPort.getAverageRating(id);
            return book.withRating(rating);
        });
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public Book updateStock(Long id, int quantity) {
        Book book = bookRepositoryPort.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        int newStock = book.stock() - quantity;
        if (newStock < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        Book updatedBook = book.withStock(newStock);
        return bookRepositoryPort.save(updatedBook);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        Book book = bookRepositoryPort.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        Category category = categoryRepositoryPort.findById(updatedBook.category().id())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Book newBook = bookMapper.mergeForUpdate(book.id(), book.version(), updatedBook, category);
        return bookRepositoryPort.save(newBook);
    }

    @Override
    @Transactional
    public Book applyDiscount(Long id, double percentage) {
        Book book = bookRepositoryPort.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_EXISTED));

        Book discountedBook = book.applyDiscount(percentage);
        return bookRepositoryPort.save(discountedBook);
    }

    @Override
    @Transactional
    public void processOrderCreatedEvent(com.example.bookstore.book.application.dto.OrderCreatedEvent event) {
        if (event.getEventId() == null) {
            // Fallback if eventId is missing, though we should log a warning
            updateStock(event.getBookId(), event.getQuantity());
            return;
        }

        boolean alreadyProcessed = processedEventRepository.existsById(event.getEventId());
        if (alreadyProcessed) {
            throw new RuntimeException("Duplicate event ignored: " + event.getEventId());
        }

        // 1. Trừ kho
        updateStock(event.getBookId(), event.getQuantity());

        // 2. Lưu lại eventId để chống trùng lặp
        com.example.bookstore.book.domain.ProcessedEvent processedEvent = com.example.bookstore.book.domain.ProcessedEvent.builder()
                .eventId(event.getEventId())
                .processedAt(java.time.LocalDateTime.now())
                .build();
        processedEventRepository.save(processedEvent);
    }
}
