package com.delivery.config;

import com.delivery.model.Role;
import com.delivery.model.Usuario;
import com.delivery.repository.RoleRepository;
import com.delivery.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(RoleRepository roleRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Cria as roles se não existirem
        Role userRole = createRoleIfNotFound("USER");
        Role adminRole = createRoleIfNotFound("ADMIN");

        // Cria o administrador padrão se não existir
        createAdminUserIfNotFound(adminRole);
    }

    private Role createRoleIfNotFound(String roleName) {
        Role role = roleRepository.findByPapel(roleName);
        if (role == null) {
            role = new Role();
            role.setPapel(roleName);
            roleRepository.save(role);
        }
        return role;
    }

    private void createAdminUserIfNotFound(Role adminRole) {
        if (usuarioRepository.findByEmail("admin@fakedata.com") == null) {
            Usuario admin = new Usuario();
            admin.setNome("Admin Padrao");
            admin.setEmail("admin@fakedata.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setCpf("00000000000");
            admin.setDataNascimento(java.time.LocalDate.of(1990, 1, 1));
            admin.setEndereco("Endereço Administrativo");
            admin.setRoles(Collections.singletonList(adminRole));
            usuarioRepository.save(admin);
        }
    }
}
