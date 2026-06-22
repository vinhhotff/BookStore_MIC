package com.example.bookstore.book.adapter.out.persistence;

import com.example.bookstore.book.domain.model.Category;
import com.example.bookstore.book.domain.port.out.CategoryRepositoryPort;
import com.example.bookstore.book.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository repository;
    @Qualifier("bookMapper")
    private final BookMapper mapper;
    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        CategoryEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }


}
