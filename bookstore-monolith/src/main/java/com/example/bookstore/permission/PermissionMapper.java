package com.example.bookstore.permission;


import com.example.bookstore.permission.PermissionRequest;
import com.example.bookstore.permission.PermissionResponse;
import com.example.bookstore.permission.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}

