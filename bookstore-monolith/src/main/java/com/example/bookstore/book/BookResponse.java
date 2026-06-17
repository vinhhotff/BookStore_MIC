package com.example.bookstore.book;

import java.io.Serial;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BookResponse(
    Long id,
    String title,
    String author,
    Double price,
    Integer stock,
    String categoryName,
    LocalDate createdAt,
    LocalDate updatedAt
) implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
