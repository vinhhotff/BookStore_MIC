package com.example.bookstore.category;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.category.CategoryRequest;
import com.example.bookstore.category.CategoryResponse;
import com.example.bookstore.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Category operations.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all categories.
     *
     * @return an ApiResponse containing the list of CategoryResponse DTOs.
     */
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.findAll())
                .build();
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the unique ID of the category.
     * @return an ApiResponse containing the CategoryResponse DTO.
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable long id) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.findById(id))
                .build();
    }

    /**
     * Creates a new category.
     * Restricted to ADMIN role.
     *
     * @param request the category creation details.
     * @return an ApiResponse containing the saved CategoryResponse DTO.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Tạo thể loại thành công")
                .result(categoryService.saveCategory(request))
                .build();
    }

    /**
     * Updates an existing category.
     * Restricted to ADMIN role.
     *
     * @param id      the unique ID of the category to update.
     * @param request the category update details.
     * @return an ApiResponse containing the updated CategoryResponse DTO.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .message("Cập nhật thể loại thành công")
                .result(categoryService.updateCategory(id, request))
                .build();
    }

    /**
     * Deletes a category by its ID.
     * Restricted to ADMIN role.
     *
     * @param id the unique ID of the category to delete.
     * @return an ApiResponse with a success message.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .message("Xóa thể loại thành công")
                .build();
    }
}
