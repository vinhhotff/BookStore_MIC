package com.example.bookstore.book.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record BookRequest(
    @NotBlank(message = "bookTitle cannot be blank")
    String title,
    String author,
    Double price,
    Integer stock,
    Long categoryId
) {}
