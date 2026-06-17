package com.example.bookstore.book;

import com.example.bookstore.book.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    // Lọc theo Tên sách (chứa từ khóa, không phân biệt hoa thường)
    public static Specification<Book> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isEmpty()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    // Lọc theo khoảng giá (Từ X đến Y)
    public static Specification<Book> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }

    // Lọc theo ID thể loại
    public static Specification<Book> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) return null;
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }
}
