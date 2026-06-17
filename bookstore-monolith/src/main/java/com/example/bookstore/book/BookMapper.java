package com.example.bookstore.book;

import com.example.bookstore.book.BookRequest;
import com.example.bookstore.book.BookResponse;
import com.example.bookstore.book.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toBook(BookRequest request);

    @Mapping(source = "category.name", target = "categoryName")
    BookResponse toBookResponse(Book book);
}
