package com.example.bookstore.book.adapter.in.web;

import lombok.Builder;

@Builder
public record BookResponse(
    Long id,
    String title,
    String author,
    Double price,
    Integer stock,
    String categoryName,
    Double rating
) {}
