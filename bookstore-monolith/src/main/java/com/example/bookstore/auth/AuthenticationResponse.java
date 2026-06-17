package com.example.bookstore.auth;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
    String token,
    boolean authenticated
) {}
