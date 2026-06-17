package com.example.bookstore.permission;

import com.example.bookstore.permission.PermissionRequest;
import com.example.bookstore.permission.PermissionResponse;
import com.example.bookstore.permission.PermissionMapper;
import com.example.bookstore.permission.Permission;
import com.example.bookstore.permission.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        // Dùng Stream để lặp qua List và biến biến Entity thành Response
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
