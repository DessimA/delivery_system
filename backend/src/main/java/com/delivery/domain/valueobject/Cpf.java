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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cpf {

    @Column(name = "cpf", length = 11)
    private String value;

    public Cpf(String value) {
        if (value == null || !isValid(value)) {
            throw new IllegalArgumentException("CPF invalido");
        }
        this.value = clean(value);
    }

    private boolean isValid(String cpf) {
        String cleaned = clean(cpf);
        if (cleaned.length() != 11 || !cleaned.matches("\\d+")) {
            return false;
        }
        if (cleaned.chars().allMatch(c -> c == cleaned.charAt(0))) {
            return false;
        }
        int[] digits = cleaned.chars().map(c -> c - '0').toArray();
        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += digits[i] * (10 - i);
        }
        int remainder1 = (sum1 * 10) % 11;
        if (remainder1 == 10) remainder1 = 0;
        if (remainder1 != digits[9]) return false;

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += digits[i] * (11 - i);
        }
        int remainder2 = (sum2 * 10) % 11;
        if (remainder2 == 10) remainder2 = 0;
        return remainder2 == digits[10];
    }

    private String clean(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    @Override
    public String toString() {
        return value;
    }
}
