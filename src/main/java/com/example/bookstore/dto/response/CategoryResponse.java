package com.example.bookstore.dto.response;

import lombok.*;

/**
 * Data Transfer Object for representing a Category in API responses.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private long id;
    private String name;
}
