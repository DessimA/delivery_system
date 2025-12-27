package com.delivery.mapper;

import com.delivery.dto.OrderResponseDTO;
import com.delivery.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderMapper {
    OrderResponseDTO toResponseDTO(Order entity);
}