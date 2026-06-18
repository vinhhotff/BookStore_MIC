package com.example.bookstore.auth;

import lombok.Builder;

@Builder
public record IntrospectRequest(
    String token
) {}
