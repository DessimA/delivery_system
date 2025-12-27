package com.delivery.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class Cpf {

    @Column(name = "cpf", length = 11)
    private String value;

    public Cpf(String value) {
        if (value == null || !isValid(value)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        this.value = clean(value);
    }

    private boolean isValid(String cpf) {
        String cleaned = clean(cpf);
        return cleaned.length() == 11 && cleaned.matches("\\d+");
    }

    private String clean(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    @Override
    public String toString() {
        return value;
    }
}
