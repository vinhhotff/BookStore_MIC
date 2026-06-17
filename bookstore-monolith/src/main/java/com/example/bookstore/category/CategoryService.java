package com.example.bookstore.category;

import com.example.bookstore.category.CategoryRequest;
import com.example.bookstore.category.CategoryResponse;

import java.util.List;

/**
 * Service interface defining business operations for Category management.
 */
public interface CategoryService {


    List<CategoryResponse> findAll();

    CategoryResponse findById(long id);

    CategoryResponse saveCategory(CategoryRequest request);

    CategoryResponse updateCategory(long id, CategoryRequest request);

    void deleteCategory(long id);
}
