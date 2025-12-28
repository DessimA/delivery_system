package com.delivery.service;

import com.delivery.dto.UserRequestDTO;
import com.delivery.dto.UserResponseDTO;
import com.delivery.mapper.UserMapper;
import com.delivery.model.Role;
import com.delivery.model.User;
import com.delivery.repository.RoleRepository;
import com.delivery.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest extends AbstractServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Deve criar um usuario com sucesso")
    void shouldCreateUserSuccessfully() {
        // Given
        UserRequestDTO request = new UserRequestDTO(
            "Test", "12345678901", LocalDate.of(1990, 1, 1), "Address", "test@test.com", "password"
        );
        User user = new User();
        Role role = new Role();
        role.setName("USER");

        when(userRepository.findByEmailAddress(anyString())).thenReturn(null);
        when(userMapper.toEntity(any())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(roleRepository.findByName("USER")).thenReturn(role);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(any())).thenReturn(mock(UserResponseDTO.class));

        // When
        UserResponseDTO result = userService.createUser(request);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).save(any());
    }
}