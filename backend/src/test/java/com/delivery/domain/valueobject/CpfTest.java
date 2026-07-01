package com.delivery.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfTest {

    @Test
    @DisplayName("Deve criar CPF valido quando numero for valido")
    void shouldCreateCpfWhenValid() {
        String validCpf = "52998224725";
        Cpf cpf = new Cpf(validCpf);
        assertThat(cpf.getValue()).isEqualTo(validCpf);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "1234567890", "123456789012", "abc", "", "11111111111", "00000000000"})
    @DisplayName("Deve lancar excecao quando CPF for invalido")
    void shouldThrowExceptionWhenInvalid(String invalidCpf) {
        assertThatThrownBy(() -> new Cpf(invalidCpf))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF invalido");
    }

    @Test
    @DisplayName("Deve ignorar formatacao ao criar CPF")
    void shouldCleanFormatting() {
        Cpf cpf = new Cpf("529.982.247-25");
        assertThat(cpf.getValue()).isEqualTo("52998224725");
    }
}
