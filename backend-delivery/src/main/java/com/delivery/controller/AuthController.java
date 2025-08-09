package com.delivery.controller;

import com.delivery.dto.LoginRequestDTO;
import com.delivery.dto.LoginResponseDTO;
import com.delivery.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Operation(summary = "Autentica um usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getSenha()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new LoginResponseDTO(jwt));
    }
}
