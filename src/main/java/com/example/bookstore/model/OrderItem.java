package com.example.bookstore.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_order_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mặt hàng này thuộc về hóa đơn nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // Mặt hàng này là cuốn sách nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    // Mua bao nhiêu cuốn
    private int quantity;

    // QUAN TRỌNG: Phải lưu lại giá tiền TẠI THỜI ĐIỂM MUA. 
    // Vì nếu năm sau Sách tăng giá, hóa đơn cũ không được đổi giá theo!
    private double priceAtPurchase;
}
