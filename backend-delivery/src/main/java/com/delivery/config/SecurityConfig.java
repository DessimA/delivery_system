package com.delivery.config;

import com.delivery.security.JwtAuthenticationEntryPoint;
import com.delivery.security.JwtAuthenticationFilter;
import com.delivery.security.UserDetailsServiceImplementacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${frontend.url}")
    private String[] allowedOrigins;

    @Autowired
    private UserDetailsServiceImplementacao userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Rotas públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/produtos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/estabelecimentos/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/swagger-ui.html").permitAll()

                // Rotas de administrador/restaurante
                .requestMatchers(HttpMethod.POST, "/api/produtos").hasAnyAuthority("ROLE_ADMIN", "ROLE_RESTAURANT")
                .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RESTAURANT")
                .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_RESTAURANT")
                .requestMatchers(HttpMethod.POST, "/api/estabelecimentos").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/estabelecimentos/**").hasAuthority("ROLE_RESTAURANT")
                .requestMatchers(HttpMethod.DELETE, "/api/estabelecimentos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/pedidos").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/restaurante/**").hasAuthority("ROLE_RESTAURANT")

                // Rotas de entregador
                .requestMatchers(HttpMethod.POST, "/api/entregas").hasAuthority("ROLE_ADMIN") // Apenas ADMIN cria entregas
                .requestMatchers(HttpMethod.GET, "/api/entregas/disponiveis").hasAuthority("ROLE_DELIVERY")
                .requestMatchers(HttpMethod.POST, "/api/entregas/{id}/aceitar").hasAuthority("ROLE_DELIVERY")
                .requestMatchers(HttpMethod.PUT, "/api/entregas/{id}/status").hasAnyAuthority("ROLE_ADMIN", "ROLE_DELIVERY")
                .requestMatchers(HttpMethod.GET, "/api/entregas/minhas").hasAuthority("ROLE_DELIVERY")

                // Rotas de pagamento
                .requestMatchers(HttpMethod.POST, "/api/pagamentos/processar").authenticated()

                // Rotas de usuário autenticado
                .requestMatchers("/api/usuarios/me/**").authenticated()
                .requestMatchers("/api/pedidos/**").authenticated()

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
