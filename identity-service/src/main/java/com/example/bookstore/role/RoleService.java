package com.example.bookstore.role;

import com.example.bookstore.permission.Permission;

import com.example.bookstore.role.RoleRequest;
import com.example.bookstore.role.RoleResponse;
import com.example.bookstore.role.RoleMapper;
import com.example.bookstore.permission.PermissionRepository;
import com.example.bookstore.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        // Moi móc các Permission từ Database lên dựa theo cái list chữ truyền vào
        var permissions = permissionRepository.findAllById(request.permissions());
        role.setPermissions(new java.util.HashSet<>(permissions)); // Gán các Quyền cho Role

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
