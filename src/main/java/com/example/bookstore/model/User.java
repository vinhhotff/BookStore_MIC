package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "app_users")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Dùng chuỗi UUID cho bảo mật
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password; // Mật khẩu đã mã hóa
    private String firstName;
    private String lastName;

    @ManyToMany // Lưu danh sách Role đơn giản
    private Set<Role> roles;

    @Version
    private Integer version;
}