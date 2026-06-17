package com.example.bookstore.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CategoryRequest(
    @NotBlank(message = "Category name cannot be blank")
    String name
) {}
