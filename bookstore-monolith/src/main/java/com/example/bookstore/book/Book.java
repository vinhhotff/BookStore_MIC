package com.example.bookstore.book;

import com.example.bookstore.category.Category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "books")
@SoftDelete(columnName = "deleted")
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Book name cannot be blank")
    private String title;

    @NotBlank(message = "Author name cannot be blank")
    private String author;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Integer version;


    // 1. Constructor không đối số (Bắt buộc cho JPA)
    public Book() {
    }

    // 2. Constructor đầy đủ đối số (Thường dùng để khởi tạo nhanh)
    public Book(Long id, String title, String author, double price, Category category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.category = category;
    }

    // --- GETTERS & SETTERS ---

    // Override toString (Tùy chọn, giúp debug dễ hơn)
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", price=" + price +
                '}';
    }
}
