package com.example.bookstore.order;

import com.example.bookstore.order.OrderItemResponse;
import com.example.bookstore.order.OrderResponse;
import com.example.bookstore.order.Order;
import com.example.bookstore.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct Mapper for converting between Order/OrderItem entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Maps an Order entity to an OrderResponse DTO.
     */
    OrderResponse toOrderResponse(Order order);

    /**
     * Maps an OrderItem entity to an OrderItemResponse DTO.
     * Maps nested book properties to flat DTO fields.
     */
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
