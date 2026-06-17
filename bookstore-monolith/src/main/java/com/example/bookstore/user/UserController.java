package com.example.bookstore.user;

import com.example.bookstore.common.ApiResponse;
import com.example.bookstore.user.UserCreationRequest;
import com.example.bookstore.user.UserResponse;
import com.example.bookstore.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller managing User profiles and account creation.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Exposes public REST API to register a new user account.
     *
     * @param request the user details to register.
     * @return ApiResponse containing the registered UserResponse DTO.
     */
    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    /**
     * Retrieves the profile information for the authenticated user.
     *
     * @return ApiResponse containing the UserResponse DTO.
     */
    @GetMapping("/myInfor")
    @PreAuthorize("hasRole('User')")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyinfo())
                .build();
    }
}
