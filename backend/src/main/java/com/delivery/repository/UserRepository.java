package com.delivery.repository;

import com.delivery.model.User;
import com.delivery.domain.valueobject.Cpf;
import com.delivery.domain.valueobject.Email;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles", "establishment"})
    User findByEmail(Email email);
    
    default User findByEmailAddress(String email) {
        return findByEmail(new Email(email));
    }

    User findByCpf(Cpf cpf);

    default User findByCpfValue(String cpf) {
        return findByCpf(new Cpf(cpf));
    }
}