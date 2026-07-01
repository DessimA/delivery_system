package com.delivery.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("Deve criar Email válido")
    void shouldCreateEmailWhenValid() {
        String address = "test@domain.com";
        Email email = new Email(address);
        assertThat(email.getAddress()).isEqualTo(address);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "@domain.com", "test@", "test@domain", ""})
    @DisplayName("Deve lançar exceção para email inválido")
    void shouldThrowExceptionWhenInvalid(String invalid) {
        assertThatThrownBy(() -> new Email(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail inválido");
    }
}
