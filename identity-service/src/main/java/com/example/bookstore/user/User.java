package com.example.bookstore.user;

import com.example.bookstore.role.Role;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "app_users")
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
@SoftDelete(columnName = "deleted")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Dùng chuỗi UUID cho bảo mật
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = true)
    private String email;

    private String password; // Mật khẩu đã mã hóa
    private String firstName;
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER) // Load roles eagerly for security context
    private Set<Role> roles;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Integer version;
}
