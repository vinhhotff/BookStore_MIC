package com.example.bookstore.mapper;

import com.example.bookstore.dto.request.CategoryRequest;
import com.example.bookstore.dto.response.CategoryResponse;
import com.example.bookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct Mapper for converting between Category entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Maps a CategoryRequest to a Category entity.
     * Ignore fields that are generated or populated by the system.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "version", ignore = true)
    Category toCategory(CategoryRequest request);

    /**
     * Maps a Category entity to a CategoryResponse DTO.
     */
    CategoryResponse toCategoryResponse(Category category);
}
