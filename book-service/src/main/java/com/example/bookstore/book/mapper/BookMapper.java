package com.example.bookstore.book.mapper;

import com.example.bookstore.book.adapter.in.web.BookRequest;
import com.example.bookstore.book.adapter.in.web.BookResponse;
import com.example.bookstore.book.adapter.out.persistence.BookEntity;
import com.example.bookstore.book.adapter.out.persistence.CategoryEntity;
import com.example.bookstore.book.domain.model.Book;
import com.example.bookstore.book.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    // --- Inbound Mapping (Web -> Domain) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId")
    Book toDomain(BookRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    Book updateCategory(Book book, Category category);

    default Category mapCategory(Long categoryId) {
        if (categoryId == null) return null;
        return new Category(categoryId, null);
    }

    @Mapping(target = "id", source = "oldId")
    @Mapping(target = "version", source = "oldVersion")
    @Mapping(target = "category", source = "newCategory")
    Book mergeForUpdate(Long oldId, Integer oldVersion, Book newBookDetails, Category newCategory);

    BookResponse toResponse(Book book);

    // --- Outbound Mapping (Domain <-> Entity) ---
    BookEntity toEntity(Book domain);
    Book toDomain(BookEntity entity);
    
    CategoryEntity toEntity(Category domain);
    Category toDomain(CategoryEntity entity);
}
