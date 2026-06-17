package com.example.bookstore.permission;

import lombok.Builder;

@Builder
public record PermissionResponse(
    String name,
    String description
) {}
