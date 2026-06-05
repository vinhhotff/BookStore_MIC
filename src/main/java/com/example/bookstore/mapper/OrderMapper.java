package com.example.bookstore.mapper;

import com.example.bookstore.dto.response.OrderItemResponse;
import com.example.bookstore.dto.response.OrderResponse;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
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
