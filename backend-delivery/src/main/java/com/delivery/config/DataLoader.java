package com.delivery.config;

import com.delivery.model.Role;
import com.delivery.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByPapel("USER") == null) {
            Role userRole = new Role();
            userRole.setPapel("USER");
            roleRepository.save(userRole);
        }

        if (roleRepository.findByPapel("ADMIN") == null) {
            Role adminRole = new Role();
            adminRole.setPapel("ADMIN");
            roleRepository.save(adminRole);
        }
    }
}
