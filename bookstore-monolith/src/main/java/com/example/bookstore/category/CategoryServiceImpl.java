package com.example.bookstore.category;

import com.example.bookstore.category.CategoryRequest;
import com.example.bookstore.category.CategoryResponse;
import com.example.bookstore.exception.AppException;
import com.example.bookstore.exception.ErrorCode;
import com.example.bookstore.category.CategoryMapper;
import com.example.bookstore.category.Category;
import com.example.bookstore.category.CategoryRepository;
import com.example.bookstore.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation containing business logic for Category management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> findAll() {
        log.info("Fetching all categories from database...");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public CategoryResponse findById(long id) {
        log.info("Fetching category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public CategoryResponse saveCategory(CategoryRequest request) {
        log.info("Saving new category: {}", request.name());
        Category category = categoryMapper.toCategory(request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public CategoryResponse updateCategory(long id, CategoryRequest request) {
        log.info("Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setName(request.name());
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    @CacheEvict(value = { "categories", "category" }, allEntries = true)
    public void deleteCategory(long id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }
}
