package com.example.bookstore.role;

import com.example.bookstore.permission.PermissionResponse;

import java.util.Set;
import lombok.Builder;

@Builder
public record RoleResponse(
    String name,
    String description,
    Set<PermissionResponse> permissions
) {}
