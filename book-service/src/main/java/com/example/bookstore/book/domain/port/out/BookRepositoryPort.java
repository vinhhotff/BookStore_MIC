package com.example.bookstore.book.domain.port.out;

import com.example.bookstore.book.domain.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    Book save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findAll();
}
