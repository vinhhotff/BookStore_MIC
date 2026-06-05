package com.example.bookstore.service;

import com.example.bookstore.dto.request.CategoryRequest;
import com.example.bookstore.dto.response.CategoryResponse;

import java.util.List;

/**
 * Service interface defining business operations for Category management.
 */
public interface CategoryService {

    /**
     * Retrieves all categories.
     *
     * @return a list of CategoryResponse DTOs.
     */
    List<CategoryResponse> findAll();

    /**
     * Retrieves a category by its unique identifier.
     *
     * @param id the unique ID of the category.
     * @return the CategoryResponse DTO.
     */
    CategoryResponse findById(long id);

    /**
     * Creates and saves a new category.
     *
     * @param request the category creation request details.
     * @return the saved CategoryResponse DTO.
     */
    CategoryResponse saveCategory(CategoryRequest request);

    /**
     * Updates an existing category by its ID.
     *
     * @param id      the unique ID of the category to update.
     * @param request the category update details.
     * @return the updated CategoryResponse DTO.
     */
    CategoryResponse updateCategory(long id, CategoryRequest request);

    /**
     * Deletes a category by its ID.
     *
     * @param id the unique ID of the category to delete.
     */
    void deleteCategory(long id);
}
