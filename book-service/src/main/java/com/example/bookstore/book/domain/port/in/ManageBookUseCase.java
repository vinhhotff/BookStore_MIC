package com.example.bookstore.book.domain.port.in;

import com.example.bookstore.book.domain.model.Book;
import java.util.List;
import java.util.Optional;

public interface ManageBookUseCase {
    Book createBook(Book book);
    Optional<Book> getBook(Long id);
    List<Book> getAllBooks();
    Book updateStock(Long id, int quantity);
    Book updateBook(Long id, Book updatedBook);
    Book applyDiscount(Long id, double percentage);
}
