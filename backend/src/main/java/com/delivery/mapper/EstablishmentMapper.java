package com.delivery.mapper;

import com.delivery.dto.EstablishmentRequestDTO;
import com.delivery.dto.EstablishmentResponseDTO;
import com.delivery.model.Establishment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EstablishmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", constant = "true")
    Establishment toEntity(EstablishmentRequestDTO dto);

    @Mapping(target = "active", source = "active")
    EstablishmentResponseDTO toResponseDTO(Establishment entity);
}
