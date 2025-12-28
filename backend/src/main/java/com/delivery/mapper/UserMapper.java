package com.delivery.mapper;

import com.delivery.dto.UserRequestDTO;
import com.delivery.dto.UserResponseDTO;
import com.delivery.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "establishment", ignore = true)
    @Mapping(target = "cpf.value", source = "cpf")
    @Mapping(target = "email.address", source = "email")
    User toEntity(UserRequestDTO dto);

    @Mapping(target = "cpf", source = "cpf.value")
    @Mapping(target = "email", source = "email.address")
    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    UserResponseDTO toResponseDTO(User user);

    default List<String> mapRoles(User user) {
        if (user.getRoles() == null) return List.of();
        return user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
    }
}