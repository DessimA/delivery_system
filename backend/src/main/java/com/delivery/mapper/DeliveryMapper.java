package com.delivery.mapper;

import com.delivery.dto.DeliveryResponseDTO;
import com.delivery.model.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "courierId", source = "courier.id")
    DeliveryResponseDTO toResponseDTO(Delivery entity);
}
