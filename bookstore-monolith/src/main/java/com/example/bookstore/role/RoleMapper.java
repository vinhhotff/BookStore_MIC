package com.example.bookstore.role;

import com.example.bookstore.permission.Permission;

import com.example.bookstore.role.RoleRequest;
import com.example.bookstore.role.RoleResponse;
import com.example.bookstore.role.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    // Bỏ qua cái list Permission khi copy sang, ta sẽ gán bằng tay ở Service
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
