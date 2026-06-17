package com.example.bookstore.role;

import com.example.bookstore.permission.Permission;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter 
@Setter 
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(columnName = "deleted")
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    private String name;
    private String description;

    @ManyToMany
    Set<Permission> permissions;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
