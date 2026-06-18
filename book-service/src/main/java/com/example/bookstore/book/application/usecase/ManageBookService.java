package com.example.bookstore.book.application.usecase;

import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.model.Category;
import com.example.bookstore.book.domain.port.in.ManageBookUseCase;
import com.example.bookstore.book.domain.port.out.BookRepositoryPort;
import com.example.bookstore.book.domain.port.out.CategoryRepositoryPort;
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

    @Override
    @Transactional
    public Book createBook(String title, String author, double price, int stock, Long categoryId) {
        Category category = categoryRepositoryPort.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Book book = new Book(null, title, author, price, stock, category);
        return bookRepositoryPort.save(book);
    }

    @Override
    public Optional<Book> getBook(Long id) {
        return bookRepositoryPort.findById(id);
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
}
