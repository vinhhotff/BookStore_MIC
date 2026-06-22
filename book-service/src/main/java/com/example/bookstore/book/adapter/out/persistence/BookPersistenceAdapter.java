package com.example.bookstore.book.adapter.out.persistence;

import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.model.Category;
import com.example.bookstore.book.domain.port.out.BookRepositoryPort;
import com.example.bookstore.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookPersistenceAdapter implements BookRepositoryPort {

    private final SpringDataBookRepository repository;
    private final BookMapper mapper;

    @Override
    public Book save(Book book) {
        BookEntity entity = mapper.toEntity(book);
        BookEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
