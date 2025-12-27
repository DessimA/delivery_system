package com.delivery.service;

import com.delivery.model.Establishment;
import com.delivery.repository.EstablishmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class EstablishmentServiceTest extends AbstractServiceTest {

    @Mock private EstablishmentRepository establishmentRepository;
    @InjectMocks private EstablishmentService establishmentService;

    @Test
    @DisplayName("Deve buscar estabelecimento por ID")
    void shouldFindById() {
        Establishment est = new Establishment();
        est.setId(1L);
        when(establishmentRepository.findById(1L)).thenReturn(Optional.of(est));

        Optional<Establishment> result = establishmentService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }
}
