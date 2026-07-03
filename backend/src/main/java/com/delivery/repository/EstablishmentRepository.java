package com.delivery.repository;

import com.delivery.model.Establishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
    Optional<Establishment> findByName(String name);
    List<Establishment> findByActiveTrue();
}