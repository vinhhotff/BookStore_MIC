package com.example.bookstore.user;

import com.example.bookstore.role.RoleResponse;

import java.util.Set;
import lombok.Builder;

@Builder
public record UserResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    Set<RoleResponse> roles
) {}
