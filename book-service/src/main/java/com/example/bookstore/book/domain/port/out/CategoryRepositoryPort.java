package com.example.bookstore.book.domain.port.out;

import com.example.bookstore.book.domain.model.Category;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(Long id);
    Optional<Category> findByName(String name);
}
