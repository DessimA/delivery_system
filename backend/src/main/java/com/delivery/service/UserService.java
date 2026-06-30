package com.delivery.service;

import com.delivery.dto.UserRequestDTO;
import com.delivery.dto.UserResponseDTO;
import com.delivery.exception.EmailAlreadyExistsException;
import com.delivery.mapper.UserMapper;
import com.delivery.model.Role;
import com.delivery.model.User;
import com.delivery.repository.RoleRepository;
import com.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.findByEmailAddress(dto.email()) != null) {
            throw new EmailAlreadyExistsException("Email ja cadastrado.");
        }

        if (dto.password() == null || dto.password().isBlank()) {
            throw new IllegalArgumentException("Password is required for registration");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        
        Role userRole = roleRepository.findByName("USER");
        user.setRoles(Collections.singletonList(userRole));

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO findByEmail(String email) {
        User user = userRepository.findByEmailAddress(email);
        return userMapper.toResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateProfile(String email, UserRequestDTO dto) {
        User user = userRepository.findByEmailAddress(email);
        if (user == null) {
            throw new com.delivery.exception.UserNotFoundException("Usuario nao encontrado");
        }

        user.setName(dto.name());
        user.setCpf(new com.delivery.domain.valueobject.Cpf(dto.cpf()));
        user.setBirthDate(dto.birthDate());
        user.setAddress(dto.address());

        if (dto.email() != null && !dto.email().isBlank() && !dto.email().equals(email)) {
            if (userRepository.findByEmailAddress(dto.email()) != null) {
                throw new EmailAlreadyExistsException("Email ja cadastrado.");
            }
            user.setEmail(new com.delivery.domain.valueobject.Email(dto.email()));
        }
        
        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        return userMapper.toResponseDTO(userRepository.save(user));
    }
}