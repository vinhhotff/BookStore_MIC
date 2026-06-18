package com.example.bookstore.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserCreationRequest(
    @Size(min = 3, message = "Username phải từ 3 ký tự")
    String username,
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    String email,
    @Size(min = 8, message = "Password phải từ 8 ký tự")
    String password,
    String firstName,
    String lastName
) {}
