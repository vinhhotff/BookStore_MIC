package com.example.bookstore.user;

import com.example.bookstore.role.RoleMapper;

import com.example.bookstore.user.UserCreationRequest;
import com.example.bookstore.user.UserResponse;
import com.example.bookstore.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);
}
