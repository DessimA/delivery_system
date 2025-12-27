package com.delivery.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfTest {

    @Test
    @DisplayName("Deve criar CPF válido quando número for válido")
    void shouldCreateCpfWhenValid() {
        String validCpf = "12345678901";
        Cpf cpf = new Cpf(validCpf);
        assertThat(cpf.getValue()).isEqualTo(validCpf);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "1234567890", "123456789012", "abc", ""})
    @DisplayName("Deve lançar exceção quando CPF for inválido")
    void shouldThrowExceptionWhenInvalid(String invalidCpf) {
        assertThatThrownBy(() -> new Cpf(invalidCpf))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF inválido");
    }

    @Test
    @DisplayName("Deve ignorar formatação ao criar CPF")
    void shouldCleanFormatting() {
        Cpf cpf = new Cpf("123.456.789-01");
        assertThat(cpf.getValue()).isEqualTo("12345678901");
    }
}
