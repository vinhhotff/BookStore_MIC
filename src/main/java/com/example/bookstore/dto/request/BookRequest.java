package com.example.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BookRequest {
    @NotBlank(message = "bookTitle cannot blank")
    String title;
    String author;
    Double price;
    Integer stock;
    Long categoryId;
}
