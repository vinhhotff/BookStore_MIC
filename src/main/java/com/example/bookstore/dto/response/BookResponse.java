package com.example.bookstore.dto.response;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;

@Value
@Builder
public class BookResponse {
    Long id;
    String title;
    String author;
    Double price;
    Integer stock;
    String categoryName;
    LocalDate createdAt;
    LocalDate updatedAt;
}
