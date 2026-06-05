package com.example.bookstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Data Transfer Object for receiving Category creation and update requests.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name cannot be blank")
    private String name;
}
