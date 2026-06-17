package com.example.bookstore.book;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookRequest(
    @NotBlank(message = "bookTitle cannot blank")
    String title,
    String author,
    Double price,
    Integer stock,
    Long categoryId
) {}
