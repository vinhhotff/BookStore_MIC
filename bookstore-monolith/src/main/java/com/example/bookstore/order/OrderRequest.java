package com.example.bookstore.order;

import com.example.bookstore.book.Book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderRequest(
    @NotNull(message = "Book ID cannot be null")
    Long bookId,
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}
