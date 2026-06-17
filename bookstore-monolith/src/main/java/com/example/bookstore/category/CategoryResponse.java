package com.example.bookstore.category;

import java.io.Serial;
import lombok.Builder;

@Builder
public record CategoryResponse(
    long id,
    String name
) implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
