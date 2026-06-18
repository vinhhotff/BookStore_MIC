package com.example.bookstore.user;



import com.example.bookstore.user.UserCreationRequest;
import com.example.bookstore.user.UserResponse;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    UserResponse getMyinfo();
}
