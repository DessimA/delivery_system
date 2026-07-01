package com.delivery.mapper;

import com.delivery.dto.OrderResponseDTO;
import com.delivery.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, DeliveryMapper.class})
public interface OrderMapper {
    @Mapping(target = "total", source = "totalValue")
    OrderResponseDTO toResponseDTO(Order entity);
}