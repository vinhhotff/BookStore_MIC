package com.example.bookstore.book.domain.port.in;

import com.example.bookstore.book.domain.model.Book;
import java.util.List;
import java.util.Optional;

public interface ManageBookUseCase {
    Book createBook(String title, String author, double price, int stock, Long categoryId);
    Optional<Book> getBook(Long id);
    List<Book> getAllBooks();
    Book updateStock(Long id, int quantity);
}
