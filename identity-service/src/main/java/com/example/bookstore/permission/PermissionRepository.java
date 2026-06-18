package com.example.bookstore.permission;

import com.example.bookstore.permission.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
}
