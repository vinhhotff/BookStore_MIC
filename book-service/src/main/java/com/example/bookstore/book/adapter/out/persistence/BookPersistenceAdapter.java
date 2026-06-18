package com.example.bookstore.book.adapter.out.persistence;

import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.model.Category;
import com.example.bookstore.book.domain.port.out.BookRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookPersistenceAdapter implements BookRepositoryPort {

    private final SpringDataBookRepository repository;

    @Override
    public Book save(Book book) {
        BookEntity entity = toEntity(book);
        BookEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Book toDomain(BookEntity entity) {
        Category category = null;
        if (entity.getCategory() != null) {
            category = new Category(entity.getCategory().getId(), entity.getCategory().getName());
        }
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getPrice(),
                entity.getStock(),
                category
        );
    }

    private BookEntity toEntity(Book domain) {
        if (domain == null) return null;
        
        CategoryEntity categoryEntity = null;
        if (domain.category() != null) {
            categoryEntity = CategoryEntity.builder()
                    .id(domain.category().id())
                    .name(domain.category().name())
                    .build();
        }

        return BookEntity.builder()
                .id(domain.id())
                .title(domain.title())
                .author(domain.author())
                .price(domain.price())
                .stock(domain.stock())
                .category(categoryEntity)
                .build();
    }
}
