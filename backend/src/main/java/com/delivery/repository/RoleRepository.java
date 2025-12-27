package com.delivery.repository;

import com.delivery.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByPapel(String papel);
}
