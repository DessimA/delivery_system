package com.delivery.config;

import com.delivery.domain.valueobject.Cpf;
import com.delivery.domain.valueobject.Email;
import com.delivery.model.*;
import com.delivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EstablishmentRepository establishmentRepository;
    private final ProductRepository productRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN");
        Role restaurantRole = roleRepository.findByName("RESTAURANT");

        if (adminRole != null) {
            createAdminUser(adminRole);
        }
        if (restaurantRole != null) {
            createPizzaria(restaurantRole);
        }
    }

    private void createAdminUser(Role adminRole) {
        String email = "admin@fakedata.com";
        if (userRepository.findByEmailAddress(email) == null) {
            User admin = User.builder()
                    .name("Admin System")
                    .email(new Email(email))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .cpf(new Cpf("52998224725"))
                    .birthDate(java.time.LocalDate.of(1990, 1, 1))
                    .address("Admin Street, 1")
                    .roles(Collections.singletonList(adminRole))
                    .build();
            userRepository.save(admin);
        }
    }

    private void createPizzaria(Role restaurantRole) {
        String email = "pizzaria@fakedata.com";
        if (establishmentRepository.findByName("Pizzaria Bella Vista").isEmpty()) {
            Establishment pizzaria = Establishment.builder()
                    .name("Pizzaria Bella Vista")
                    .address("Rua da Pizza, 123")
                    .active(true)
                    .build();

            User owner = User.builder()
                    .name("Owner Pizzaria")
                    .email(new Email(email))
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .cpf(new Cpf("93541134780"))
                    .birthDate(java.time.LocalDate.of(1985, 5, 15))
                    .address("Rua da Pizza, 123")
                    .roles(Collections.singletonList(restaurantRole))
                    .establishment(pizzaria)
                    .build();
            
            pizzaria.setUser(owner);
            userRepository.save(owner);

            createProduct("Pizza Calabresa", "Traditional", BigDecimal.valueOf(35.0), pizzaria);
        }
    }

    private void createProduct(String name, String desc, BigDecimal price, Establishment est) {
        Product product = Product.builder()
                .name(name)
                .description(desc)
                .price(price)
                .establishment(est)
                .build();
        productRepository.save(product);
    }
}