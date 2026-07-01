package com.delivery.service;

import com.delivery.exception.UserNotFoundException;
import com.delivery.model.User;
import com.delivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("No authenticated user found.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        String email = authentication.getName();
        User user = userRepository.findByEmailAddress(email);
        if (user == null) {
            throw new UserNotFoundException("Inconsistencia de dados: usuario autenticado nao encontrado: " + email);
        }
        return user;
    }

    public boolean hasRole(User user, String role) {
        if (user == null || role == null) return false;
        return user.getRoles().stream()
                .anyMatch(r -> r.getAuthority().equals(role));
    }

    public boolean isAdmin(User user) {
        return hasRole(user, "ROLE_ADMIN");
    }

    public void verifyOwnerOrAdmin(Long resourceOwnerId) {
        User user = getAuthenticatedUser();
        if (!isAdmin(user) && !Objects.equals(user.getId(), resourceOwnerId)) {
            throw new SecurityException("Voce nao tem permissao para acessar este recurso.");
        }
    }

    public void verifyRole(String role) {
        User user = getAuthenticatedUser();
        if (!hasRole(user, role)) {
            throw new SecurityException("Usuario nao autorizado. Requer papel: " + role);
        }
    }
}