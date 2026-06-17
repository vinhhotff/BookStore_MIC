package com.example.bookstore.role;

import java.util.Set;
import lombok.Builder;

@Builder
public record RoleRequest(
    String name,
    String description,
    Set<String> permissions
) {}
