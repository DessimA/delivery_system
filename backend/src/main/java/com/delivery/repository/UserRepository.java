package com.delivery.repository;

import com.delivery.model.User;
import com.delivery.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(Email email);
    
    default User findByEmailAddress(String email) {
        return findByEmail(new Email(email));
    }
}