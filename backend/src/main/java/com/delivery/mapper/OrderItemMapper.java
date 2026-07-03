package com.delivery.mapper;

import com.delivery.dto.OrderItemResponseDTO;
import com.delivery.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "productId", source = "product.id")
    OrderItemResponseDTO toResponseDTO(OrderItem entity);
}
