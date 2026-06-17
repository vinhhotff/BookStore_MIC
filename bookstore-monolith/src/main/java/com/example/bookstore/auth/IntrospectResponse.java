package com.example.bookstore.auth;

import lombok.Builder;

@Builder
public record IntrospectResponse(
    boolean valid
) {}
