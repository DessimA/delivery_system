package com.delivery.service;

import com.delivery.model.Establishment;
import com.delivery.model.User;
import com.delivery.repository.EstablishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final SecurityService securityService;

    public List<Establishment> findAll() {
        return establishmentRepository.findByActiveTrue();
    }

    public Optional<Establishment> findById(Long id) {
        return establishmentRepository.findById(id);
    }

    public Establishment findMyEstablishment() {
        User user = securityService.getAuthenticatedUser();
        Establishment establishment = user.getEstablishment();
        if (establishment == null) {
            throw new IllegalStateException("Usuario nao possui um estabelecimento vinculado.");
        }
        return establishment;
    }

    @Transactional
    public Establishment save(Establishment establishment) {
        return establishmentRepository.save(establishment);
    }

    @Transactional
    public void delete(Long id) {
        establishmentRepository.deleteById(id);
    }
}
