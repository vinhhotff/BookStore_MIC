package com.example.bookstore.permission;

import lombok.Builder;

@Builder
public record PermissionRequest(
    String name,
    String description
) {}
